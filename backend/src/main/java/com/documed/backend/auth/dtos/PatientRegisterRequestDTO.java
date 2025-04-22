package com.documed.backend.auth.dtos;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatientRegisterRequestDTO {

  @NotBlank(message = "Imię jest wymagane") private String firstName;

  @NotBlank(message = "Nazwisko jest wymagane") private String lastName;

  @Pattern(regexp = "^\\d{11}$", message = "PESEL musi mieć dokładnie 11 cyfr") @NotBlank(message = "PESEL jest wymagany") private String pesel;

  @Pattern(regexp = "^\\d{9}$", message = "Numer telefonu musi mieć dokładnie 9 cyfr") @NotBlank(message = "Numer telefonu jest wymagany") private String phoneNumber;

  @Email(message = "Nieprawidłowy adres email") @NotBlank(message = "Adres email jest wymagany") private String email;

  @NotBlank(message = "Adres jest wymagany") private String address;

  @Size(min = 6, message = "Hasło musi mieć co najmniej 6 znaków") @NotBlank(message = "Hasło jest wymagane") private String password;

  @NotBlank(message = "Wpisz hasło ponownie") private String confirmPassword;

  @NotNull(message = "Data urodzenia jest wymagana") @Past(message = "Nieprawidłowa data urodzenia") private LocalDate birthdate;
}
