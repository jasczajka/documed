package com.documed.backend.users.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
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
  @NonNull Date birthdate;
}
