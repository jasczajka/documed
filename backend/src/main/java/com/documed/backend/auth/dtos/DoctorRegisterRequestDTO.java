package com.documed.backend.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DoctorRegisterRequestDTO {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Imię jest wymagane") private String firstName;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Nazwisko jest wymagane") private String lastName;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @Pattern(regexp = "^\\d{7}$", message = "PWZ musi mieć dokładnie 7 cyfr") @NotBlank(message = "PWZ jest wymagany") private String pwz;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @Pattern(regexp = "^\\d{9}$", message = "Numer telefonu musi mieć dokładnie 9 cyfr") @NotBlank(message = "Numer telefonu jest wymagany") private String phoneNumber;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @Email(message = "Nieprawidłowy adres email") @NotBlank(message = "Adres email jest wymagany") private String email;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @Size(min = 6, message = "Hasło musi mieć co najmniej 6 znaków") @NotBlank(message = "Hasło jest wymagane") private String password;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotEmpty(message = "Musisz podać przynajmniej jedną specjalizację") private List<@NotNull(message = "ID specjalizacji nie może być puste") Integer> specializationIds;
}
