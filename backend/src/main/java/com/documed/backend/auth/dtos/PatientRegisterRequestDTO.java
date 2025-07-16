package com.documed.backend.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatientRegisterRequestDTO {

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Imię jest wymagane") private String firstName;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Nazwisko jest wymagane") private String lastName;

  @Pattern(regexp = "^\\d{11}$", message = "PESEL musi mieć dokładnie 11 cyfr") private String pesel;

  @Pattern(
      regexp = "^[A-Za-z0-9]{9}$",
      message = "Numer paszportu musi składać się z 9 liter lub cyfr")
  private String passportNumber;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @Pattern(regexp = "^\\d{9}$", message = "Numer telefonu musi mieć dokładnie 9 cyfr") @NotBlank(message = "Numer telefonu jest wymagany") private String phoneNumber;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @Email(message = "Nieprawidłowy adres email") @NotBlank(message = "Adres email jest wymagany") private String email;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Adres jest wymagany") private String address;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @Size(min = 6, message = "Hasło musi mieć co najmniej 6 znaków") @NotBlank(message = "Hasło jest wymagane") private String password;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Wpisz hasło ponownie") private String confirmPassword;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "Data urodzenia jest wymagana") @Past(message = "Nieprawidłowa data urodzenia") private LocalDate birthdate;
}
