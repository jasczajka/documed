package com.documed.backend.auth;

import com.documed.backend.auth.dtos.AuthResponseDTO;
import com.documed.backend.auth.exceptions.AuthServiceException;
import com.documed.backend.auth.exceptions.InvalidCredentialsException;
import com.documed.backend.auth.exceptions.UserAlreadyExistsException;
import com.documed.backend.auth.exceptions.UserNotFoundException;
import com.documed.backend.users.*;
import com.documed.backend.users.model.AccountStatus;
import com.documed.backend.users.model.User;
import com.documed.backend.users.model.UserRole;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
  private final UserDAO userDAO;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  private final UserService userService;

  private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

  public AuthService(
      UserDAO userDAO, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, UserService userService) {
    this.userDAO = userDAO;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
    this.userService = userService;
  }

  public AuthResponseDTO registerPatient(
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
      if (userDAO.getByEmail(email).isPresent()
          || (pesel != null && userDAO.getByPesel(pesel).isPresent())) {
        throw new UserAlreadyExistsException("User with given email or PESEL already exists");
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
              .accountStatus(AccountStatus.ACTIVE)
              .role(UserRole.valueOf(role))
              .birthDate(Date.valueOf(birthDate))
              .build();

      User createdUser = userDAO.createAndReturn(user);
      String token = jwtUtil.generateToken(createdUser.getId(), createdUser.getRole().name());

      return AuthResponseDTO.builder()
          .token(token)
          .userId(createdUser.getId())
          .role(createdUser.getRole())
          .build();
    } catch (DataAccessException e) {
      logger.error("Error in registerPatient()", e);
      throw new AuthServiceException("Database error during registration", e);
    }
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
      String token = jwtUtil.generateToken(createdUser.getId(), createdUser.getRole().name());

      return AuthResponseDTO.builder()
          .token(token)
          .userId(createdUser.getId())
          .role(createdUser.getRole())
          .build();
    } catch (DataAccessException e) {
      logger.error("Error in registerDoctor()", e);
      throw new AuthServiceException("Database error during registration", e);
    }
  }

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
      String token = jwtUtil.generateToken(createdUser.getId(), createdUser.getRole().name());

      return AuthResponseDTO.builder()
          .token(token)
          .userId(createdUser.getId())
          .role(createdUser.getRole())
          .build();
    } catch (DataAccessException e) {
      logger.error("Error in registerStaff()", e);
      throw new AuthServiceException("Database error during registration", e);
    }
  }

  public AuthResponseDTO loginUser(String login, String password) {
    try {
      User user =
          userDAO
              .getByEmail(login)
              .or(() -> userDAO.getByPesel(login))
              .orElseThrow(() -> new UserNotFoundException("User not found"));

      if (!passwordEncoder.matches(password, user.getPassword())) {
        throw new InvalidCredentialsException("Invalid credentials");
      }

      String token = jwtUtil.generateToken(user.getId(), user.getRole().name());

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
}
