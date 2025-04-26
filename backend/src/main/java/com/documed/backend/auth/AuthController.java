package com.documed.backend.auth;

import com.documed.backend.auth.annotations.AdminOnly;
import com.documed.backend.auth.annotations.SelfDataOnly;
import com.documed.backend.auth.dtos.*;
import com.documed.backend.auth.model.OtpPurpose;
import com.documed.backend.users.UserService;
import com.documed.backend.users.model.User;
import com.documed.backend.users.model.UserRole;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
  private static final String JWT_COOKIE_NAME = "JwtToken";

  private final AuthService authService;
  private final UserService userService;
  private final OtpService otpService;

  private final JwtUtil jwtUtil;

  public AuthController(
      AuthService authService, UserService userService, OtpService otpService, JwtUtil jwtUtil) {
    this.authService = authService;
    this.userService = userService;
    this.otpService = otpService;
    this.jwtUtil = jwtUtil;
  }

  @PostMapping("/request-registration")
  public ResponseEntity<PendingUserDTO> requestRegistration(
      @Valid @RequestBody PatientRegisterRequestDTO request) {
    logger.info("Registration request for email: {}", request.getEmail());

    User createdUser =
        authService.registerPatient(
            request.getFirstName(),
            request.getLastName(),
            request.getEmail(),
            request.getPesel(),
            request.getPassword(),
            String.valueOf(UserRole.PATIENT),
            request.getPhoneNumber(),
            request.getAddress(),
            request.getBirthdate());

    PendingUserDTO responseDto =
        PendingUserDTO.builder()
            .id(createdUser.getId())
            .firstName(createdUser.getFirstName())
            .lastName(createdUser.getLastName())
            .email(createdUser.getEmail())
            .pesel(createdUser.getPesel())
            .phoneNumber(createdUser.getPhoneNumber())
            .address(createdUser.getAddress())
            .birthDate(createdUser.getBirthDate())
            .role(createdUser.getRole())
            .accountStatus(createdUser.getAccountStatus())
            .build();

    logger.debug("Pending user created with ID: {}", createdUser.getId());
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseDto);
  }

  @PostMapping("/confirm-registration")
  public ResponseEntity<Void> confirmRegistration(
      @Valid @RequestBody ConfirmRegistrationRequestDTO request,
      HttpServletResponse servletResponse) {
    logger.info("Registration confirmation for email: {}", request.getEmail());

    AuthResponseDTO authResponse =
        authService.confirmRegistration(request.getEmail(), request.getOtp());

    Cookie jwtCookie = new Cookie(JWT_COOKIE_NAME, authResponse.getToken());
    jwtCookie.setHttpOnly(true);
    jwtCookie.setPath("/");
    jwtCookie.setMaxAge(60 * 60 * 24 * 7); // 1 week
    jwtCookie.setAttribute("SameSite", "None");
    jwtCookie.setSecure(true);
    servletResponse.addCookie(jwtCookie);

    logger.info("User registration confirmed for ID: {}", authResponse.getUserId());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/request-password-reset")
  public ResponseEntity<Void> requestPasswordReset(
      @Valid @RequestBody ResetPasswordRequestDTO request) {
    logger.info("Password reset request for email: {}", request.getEmail());

    otpService.generateOtp(request.getEmail(), OtpPurpose.PASSWORD_RESET);
    logger.info("OTP generated for password reset request for email: {}", request.getEmail());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/reset-password")
  public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordConfirmDTO request) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Integer userId = (Integer) authentication.getPrincipal();
    logger.info("Password reset request for user id: {}", userId);

    authService.resetPassword(request.getEmail(), request.getOtp());

    logger.info("Password reset successful for email: {}", request.getEmail());
    return ResponseEntity.ok().build();
  }

  @SelfDataOnly
  @PostMapping("/change-password")
  public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequestDTO request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Integer userId = (Integer) authentication.getPrincipal();
    logger.info("Password change request for user ID: {}", userId);

    authService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
    logger.info("Password changed successfully for user ID: {}", userId);
    return ResponseEntity.ok().build();
  }

  @AdminOnly
  @PostMapping("/register_doctor")
  public ResponseEntity<AuthResponseDTO> registerDoctor(
      @Valid @RequestBody DoctorRegisterRequestDTO request) {
    logger.info("Registration attempt for email: {}", request.getEmail());
    AuthResponseDTO response =
        authService.registerDoctor(
            request.getFirstName(),
            request.getLastName(),
            request.getEmail(),
            request.getPwz(),
            request.getPassword(),
            request.getPhoneNumber(),
            request.getSpecializationIds());

    logger.info("Doctor registered successfully with ID: {}", response.getUserId());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @AdminOnly
  @PostMapping("/register_staff")
  public ResponseEntity<AuthResponseDTO> registerStaff(
      @Valid @RequestBody StaffRegisterRequestDTO request) {
    logger.info("Registration attempt for email: {}", request.getEmail());
    AuthResponseDTO response =
        authService.registerStaff(
            request.getFirstName(),
            request.getLastName(),
            request.getEmail(),
            request.getPassword(),
            String.valueOf(request.getRole()));

    logger.info("Staff user registered successfully with ID: {}", response.getUserId());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/login")
  public ResponseEntity<Void> login(
      @Valid @RequestBody LoginRequestDTO request, HttpServletResponse servletResponse) {
    logger.info("Login attempt for: {}", request.getLogin());

    AuthResponseDTO authResponse = authService.loginUser(request.getLogin(), request.getPassword());
    logger.debug("Login successful for user ID: {}", authResponse.getUserId());

    Cookie jwtCookie = new Cookie(JWT_COOKIE_NAME, authResponse.getToken());
    jwtCookie.setHttpOnly(true);
    jwtCookie.setPath("/");
    jwtCookie.setMaxAge(60 * 60 * 24 * 7); // 1 week
    jwtCookie.setAttribute("SameSite", "None");
    jwtCookie.setSecure(true);

    servletResponse.addCookie(jwtCookie);
    logger.debug("JWT cookie set for user {}", authResponse.getUserId());

    return ResponseEntity.ok().build();
  }

  @GetMapping("/me")
  public ResponseEntity<User> getCurrentUser(
      @CookieValue(name = JWT_COOKIE_NAME, required = false) String token,
      HttpServletResponse response) {

    logger.info("Current user info request");

    if (token == null || token.isEmpty()) {
      logger.debug("No JWT token found in request");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    try {
      if (!jwtUtil.validateToken(token)) {
        logger.warn("Invalid JWT token detected");
        clearJwtCookie(response);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      Integer userId = jwtUtil.extractUserId(token);
      Optional<User> user = userService.getById(userId);

      if (user.isEmpty()) {
        logger.warn("User not found for ID: {}", userId);
        clearJwtCookie(response);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      logger.debug("Returning current user info for ID: {}", userId);
      return ResponseEntity.ok(user.get());

    } catch (Exception e) {
      logger.warn("JWT processing error: {}", e.getMessage());
      clearJwtCookie(response);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(HttpServletResponse servletResponse) {
    logger.info("Logout request received");

    this.clearJwtCookie(servletResponse);
    logger.debug("JWT cookie cleared");

    return ResponseEntity.ok().build();
  }

  @PostMapping("/deactivate_account/{id}")
  public ResponseEntity<Void> deactivateAccount(@PathVariable int id) {
    logger.info("Account deactivation request for user ID: {}", id);

    Optional<User> user = userService.getById(id);
    if (user.isEmpty()) {
      logger.warn("User not found for account deactivation: ID {}", id);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    boolean deactivated = userService.deactivateUser(id);
    if (deactivated) {
      logger.info("User account deactivated successfully: ID {}", id);
      return ResponseEntity.noContent().build();
    } else {
      logger.error("Failed to deactivate account for user: ID {}", id);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  private void clearJwtCookie(HttpServletResponse response) {
    Cookie jwtCookie = new Cookie(JWT_COOKIE_NAME, "");
    jwtCookie.setHttpOnly(true);
    jwtCookie.setPath("/");
    jwtCookie.setMaxAge(0);
    jwtCookie.setAttribute("SameSite", "None");
    jwtCookie.setSecure(true);
    response.addCookie(jwtCookie);
  }
}
