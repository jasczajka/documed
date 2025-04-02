package com.documed.backend.auth;

import com.documed.backend.auth.dtos.AuthResponseDTO;
import com.documed.backend.auth.exceptions.AuthServiceException;
import com.documed.backend.auth.exceptions.InvalidCredentialsException;
import com.documed.backend.auth.exceptions.UserAlreadyExistsException;
import com.documed.backend.auth.exceptions.UserNotFoundException;
import com.documed.backend.users.*;
import java.sql.Date;
import java.time.LocalDate;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
  private final UserDAO userDAO;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  public AuthService(UserDAO userDAO, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
    this.userDAO = userDAO;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
  }

  public AuthResponseDTO registerUser(
      String firstName,
      String lastName,
      String email,
      String pesel,
      String password,
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
              // @TODO what was status?
              .status("ACTIVE")
              .role(UserRole.PATIENT)
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
      throw new AuthServiceException("Database error during login", e);
    }
  }
}
