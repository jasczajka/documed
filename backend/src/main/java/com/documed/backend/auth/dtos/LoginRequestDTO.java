package com.documed.backend.auth.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequestDTO {
  @NotBlank(message = "Login (email/PESEL) jest wymagany")
  private String login;

  @NotBlank(message = "Hasło jest wymagane") private String password;
}
