package com.documed.backend.auth.dtos;

import com.documed.backend.users.model.UserRole;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StaffRegisterRequestDTO {

  @NotNull(message = "Rola użytkownika jest wymagana") private UserRole role;

  @NotBlank(message = "Imię jest wymagane") private String firstName;

  @NotBlank(message = "Nazwisko jest wymagane") private String lastName;

  @Email(message = "Nieprawidłowy adres email") @NotBlank(message = "Adres email jest wymagany") private String email;

  @Size(min = 6, message = "Hasło musi mieć co najmniej 6 znaków") @NotBlank(message = "Hasło jest wymagane") private String password;
}
