package com.documed.backend.visits.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CalculateVisitCostDTO {

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private int patientId;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private int serviceId;
}
