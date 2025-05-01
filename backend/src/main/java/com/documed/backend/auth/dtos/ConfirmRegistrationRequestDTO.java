package com.documed.backend.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ConfirmRegistrationRequestDTO {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @Email(message = "Nieprawidłowy adres email") @NotBlank(message = "Adres email jest wymagany") private String email;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Kod OTP jest wymagany") @Size(min = 6, max = 6, message = "Kod OTP musi mieć 6 cyfr") private String otp;
}
