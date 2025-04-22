package com.documed.backend.auth.dtos;

import jakarta.validation.constraints.*;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DoctorRegisterRequestDTO {

  @NotBlank(message = "Imię jest wymagane") private String firstName;

  @NotBlank(message = "Nazwisko jest wymagane") private String lastName;

  @Pattern(regexp = "^\\d{7}$", message = "PWZ musi mieć dokładnie 7 cyfr") @NotBlank(message = "PWZ jest wymagany") private String pwz;

  @Pattern(regexp = "^\\d{9}$", message = "Numer telefonu musi mieć dokładnie 9 cyfr") @NotBlank(message = "Numer telefonu jest wymagany") private String phoneNumber;

  @Email(message = "Nieprawidłowy adres email") @NotBlank(message = "Adres email jest wymagany") private String email;

  @Size(min = 6, message = "Hasło musi mieć co najmniej 6 znaków") @NotBlank(message = "Hasło jest wymagane") private String password;

  @NotEmpty(message = "Musisz podać przynajmniej jedną specjalizację") private List<@NotNull(message = "ID specjalizacji nie może być puste") Integer> specializationIds;
}
