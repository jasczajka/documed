package com.documed.backend.auth.dtos;

import com.documed.backend.users.model.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StaffRegisterRequestDTO {

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "Rola użytkownika jest wymagana") private UserRole role;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Imię jest wymagane") private String firstName;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Nazwisko jest wymagane") private String lastName;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @Email(message = "Nieprawidłowy adres email") @NotBlank(message = "Adres email jest wymagany") private String email;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @Size(min = 6, message = "Hasło musi mieć co najmniej 6 znaków") @NotBlank(message = "Hasło jest wymagane") private String password;
}
