package com.documed.backend.auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ConfirmRegistrationRequestDTO {
  @Email(message = "Nieprawidłowy adres email") @NotBlank(message = "Adres email jest wymagany") private String email;

  @NotBlank(message = "Kod OTP jest wymagany") @Size(min = 6, max = 6, message = "Kod OTP musi mieć 6 cyfr") private String otp;
}
