package com.documed.backend.auth;

import com.documed.backend.auth.dtos.AuthResponseDTO;
import com.documed.backend.auth.dtos.LoginRequestDTO;
import com.documed.backend.auth.dtos.RegisterRequestDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
    AuthResponseDTO response =
        authService.registerUser(
            request.getFirstName(),
            request.getLastName(),
            request.getEmail(),
            request.getPesel(),
            request.getPassword(),
            request.getPhoneNumber(),
            request.getAddress(),
            request.getBirthdate());
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponseDTO> login(
      @Valid @RequestBody LoginRequestDTO request, HttpServletResponse response) {
    AuthResponseDTO authResponse = authService.loginUser(request.getLogin(), request.getPassword());
    Cookie jwtCookie = new Cookie("JwtToken", authResponse.getToken());

    jwtCookie.setHttpOnly(true);
    jwtCookie.setPath("/");
    jwtCookie.setMaxAge(60 * 60 * 24 * 7); // 1 week
    jwtCookie.setAttribute("SameSite", "Strict");

    response.addCookie(jwtCookie);

    // @TODO when implementing integration with frontend, remove returning the token in response
    // body, it will be HttpOnly
    return ResponseEntity.status(HttpStatus.OK).body(authResponse);
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(HttpServletResponse response) {
    Cookie jwtCookie = new Cookie("JwtToken", "");

    jwtCookie.setHttpOnly(true);
    jwtCookie.setPath("/");
    jwtCookie.setMaxAge(0);
    jwtCookie.setAttribute("SameSite", "Strict");

    response.addCookie(jwtCookie);

    return ResponseEntity.status(HttpStatus.OK).build();
  }

  // @TODO along with simplified UserService
  //  @PostMapping("/delete_user/{id}")
  //  public ResponseEntity<Void> delete_account() {
  //    User userToDelete = authService.
  //
  //  }
}
