package com.documed.backend.visits.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FacilityInfoReturnDTO {

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private int id;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String address;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String city;
}
