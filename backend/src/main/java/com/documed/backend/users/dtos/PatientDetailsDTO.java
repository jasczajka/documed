package com.documed.backend.users.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.*;

@Data
@Builder
public class PatientDetailsDTO {

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @Setter(AccessLevel.NONE)
  private int id;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private String firstName;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private String lastName;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private String email;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private String address;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private String phoneNumber;

  private String pesel;
  private String passportNumber;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull LocalDate birthdate;

  private Integer subscriptionId;
}
