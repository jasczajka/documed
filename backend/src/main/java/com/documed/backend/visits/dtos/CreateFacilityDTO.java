package com.documed.backend.visits.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CreateFacilityDTO {

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String address;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String city;
}
