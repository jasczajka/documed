package com.documed.backend.auth;

import com.documed.backend.auth.dtos.AuthResponseDTO;
import com.documed.backend.auth.exceptions.*;
import com.documed.backend.auth.model.CurrentUser;
import com.documed.backend.auth.model.OtpPurpose;
import com.documed.backend.exceptions.NotFoundException;
import com.documed.backend.schedules.WorkTimeService;
import com.documed.backend.users.*;
import com.documed.backend.users.model.AccountStatus;
import com.documed.backend.users.model.User;
import com.documed.backend.users.model.UserRole;
import com.documed.backend.users.services.UserService;
import com.documed.backend.visits.FacilityService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
  private final UserDAO userDAO;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  private final UserService userService;
  private final OtpService otpService;
  private final EmailService emailService;

  private final FacilityService facilityService;
  private final WorkTimeService workTimeService;

  private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

  public AuthService(
      UserDAO userDAO,
      PasswordEncoder passwordEncoder,
      JwtUtil jwtUtil,
      UserService userService,
      OtpService otpService,
      EmailService emailService,
      FacilityService facilityService,
      WorkTimeService workTimeService) {
    this.userDAO = userDAO;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
    this.userService = userService;
    this.otpService = otpService;
    this.emailService = emailService;
    this.facilityService = facilityService;
    this.workTimeService = workTimeService;
  }

  @Transactional
  public void resetPassword(String email, String otp) {
    otpService.validateOtp(email, otp, OtpPurpose.PASSWORD_RESET);

    userService.getByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));

    String newPassword = generateRandomPassword();

    String encodedPassword = passwordEncoder.encode(newPassword);
    updatePasswordByEmail(email, encodedPassword);

    sendNewPasswordEmail(email, newPassword);

    logger.info("Password reset successfully for user with email: {}", email);
  }

  @Transactional
  public User registerPatient(
      String firstName,
      String lastName,
      String email,
      String pesel,
      String password,
      String role,
      String phoneNumber,
      String address,
      LocalDate birthDate) {
    try {
      Optional<User> existingUserOpt = userService.getByEmail(email);
      if (existingUserOpt.isPresent()) {
        User existingUser = existingUserOpt.get();
        if (existingUser.getAccountStatus() == AccountStatus.PENDING_CONFIRMATION) {
          otpService.generateOtp(email, OtpPurpose.REGISTRATION);
          return existingUser;
        } else {
          throw new UserAlreadyExistsException("User with given email already exists");
        }
      }

      User user =
          User.builder()
              .firstName(firstName)
              .lastName(lastName)
              .email(email)
              .pesel(pesel)
              .phoneNumber(phoneNumber)
              .address(address)
              .password(passwordEncoder.encode(password))
              .accountStatus(AccountStatus.PENDING_CONFIRMATION)
              .role(UserRole.valueOf(role))
              .birthDate(birthDate)
              .build();

      User createdUser = userService.createPendingUser(user);

      otpService.generateOtp(email, OtpPurpose.REGISTRATION);

      return createdUser;

    } catch (DataAccessException e) {
      logger.error("Error in registerPatient()", e);
      throw new AuthServiceException("Database error during registration", e);
    }
  }

  @Transactional
  public AuthResponseDTO confirmRegistration(String email, String otp) {
    otpService.validateOtp(email, otp, OtpPurpose.REGISTRATION);

    User activatedUser = userService.activateUser(email);

    return AuthResponseDTO.builder()
        .userId(activatedUser.getId())
        .role(activatedUser.getRole())
        .build();
  }

  @Transactional
  public AuthResponseDTO registerDoctor(
      String firstName,
      String lastName,
      String email,
      String pwz,
      String password,
      String phoneNumber,
      List<Integer> specializationIds) {
    try {
      if (userDAO.getByEmail(email).isPresent()) {
        throw new UserAlreadyExistsException("User with given email  already exists");
      }

      User user =
          User.builder()
              .firstName(firstName)
              .lastName(lastName)
              .email(email)
              .pwzNumber(pwz)
              .phoneNumber(phoneNumber)
              .password(passwordEncoder.encode(password))
              .accountStatus(AccountStatus.ACTIVE)
              .role(UserRole.DOCTOR)
              .build();

      User createdUser = userDAO.createAndReturn(user);

      userService.addSpecializationsToUser(createdUser.getId(), specializationIds);

      workTimeService.createWorkTimeForNewUser(
          createdUser.getId(), UserRole.DOCTOR, getCurrentFacilityId());

      return AuthResponseDTO.builder()
          .userId(createdUser.getId())
          .role(createdUser.getRole())
          .build();
    } catch (DataAccessException e) {
      logger.error("Error in registerDoctor()", e);
      throw new AuthServiceException("Database error during registration", e);
    }
  }

  @Transactional
  public AuthResponseDTO registerStaff(
      String firstName, String lastName, String email, String password, String role) {
    try {
      if (userDAO.getByEmail(email).isPresent()) {
        throw new UserAlreadyExistsException("User with given email already exists");
      }

      User user =
          User.builder()
              .firstName(firstName)
              .lastName(lastName)
              .email(email)
              .password(passwordEncoder.encode(password))
              .accountStatus(AccountStatus.ACTIVE)
              .role(UserRole.valueOf(role))
              .build();

      User createdUser = userDAO.createAndReturn(user);

      return AuthResponseDTO.builder()
          .userId(createdUser.getId())
          .role(createdUser.getRole())
          .build();
    } catch (DataAccessException e) {
      logger.error("Error in registerStaff()", e);
      throw new AuthServiceException("Database error during registration", e);
    }
  }

  public AuthResponseDTO loginUser(String login, String password, Integer facilityId) {
    try {
      User user =
          userDAO
              .getByEmail(login)
              .or(() -> userDAO.getByPesel(login))
              .orElseThrow(() -> new UserNotFoundException("User not found"));

      if (!passwordEncoder.matches(password, user.getPassword())) {
        throw new InvalidCredentialsException("Invalid credentials");
      }

      if (user.getAccountStatus() != AccountStatus.ACTIVE) {
        throw new AccountNotActiveException("Account is not active");
      }
      facilityService
          .getById(facilityId)
          .orElseThrow(() -> new NotFoundException("Facility not found"));

      String token = jwtUtil.generateToken(user.getId(), user.getRole().name(), facilityId);

      return AuthResponseDTO.builder()
          .token(token)
          .userId(user.getId())
          .role(user.getRole())
          .build();
    } catch (DataAccessException e) {
      logger.error("Error in login()", e);
      throw new AuthServiceException("Database error during login", e);
    }
  }

  @Transactional
  public void changePassword(int userId, String oldPassword, String newPassword) {
    User user =
        userService.getById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

    if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
      throw new InvalidCredentialsException("Old password is incorrect");
    }

    String encodedPassword = passwordEncoder.encode(newPassword);
    userDAO.updatePasswordById(userId, encodedPassword);
  }

  private String generateRandomPassword() {
    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
    StringBuilder password = new StringBuilder();
    for (int i = 0; i < 12; i++) {
      int index = (int) (Math.random() * characters.length());
      password.append(characters.charAt(index));
    }
    return password.toString();
  }

  private void sendNewPasswordEmail(String email, String newPassword) {
    String subject = "Twoje nowe hasło";
    String text = "Twoje nowe hasło to: " + newPassword;

    emailService.sendEmail(email, subject, text);
  }

  @Transactional
  public void updatePasswordByEmail(String email, String encodedPassword) {
    userDAO.updatePasswordByEmail(email, encodedPassword);
  }

  public Integer getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return null;
    }
    Object principal = authentication.getPrincipal();
    if (principal instanceof CurrentUser currentUser) {
      return currentUser.getUserId();
    }
    return null;
  }

  public UserRole getCurrentUserRole() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return null;
    }
    Object principal = authentication.getPrincipal();
    if (principal instanceof CurrentUser currentUser) {
      return currentUser.getRole();
    }
    return null;
  }

  public Integer getCurrentFacilityId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return null;
    }
    Object principal = authentication.getPrincipal();
    if (principal instanceof CurrentUser currentUser) {
      return currentUser.getFacilityId();
    }
    return null;
  }
}
