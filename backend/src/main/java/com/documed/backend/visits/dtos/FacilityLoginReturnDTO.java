package com.documed.backend.visits.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class FacilityLoginReturnDTO {

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private int id;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private String address;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private String city;
}
