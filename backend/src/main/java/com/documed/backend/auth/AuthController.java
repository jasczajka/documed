package com.documed.backend.auth;

import com.documed.backend.auth.dtos.AuthResponseDTO;
import com.documed.backend.auth.dtos.LoginRequestDTO;
import com.documed.backend.auth.dtos.RegisterRequestDTO;
import jakarta.validation.Valid;
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
    return ResponseEntity.ok(response);
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
    AuthResponseDTO response = authService.loginUser(request.getLogin(), request.getPassword());
    return ResponseEntity.ok(response);
  }
}
