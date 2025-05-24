package com.documed.backend.visits.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScheduleVisitDTO {
  private final String patientInformation;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "patient ID is required") private final int patientId;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "time slot ID is required") private final int firstTimeSlotId;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "service ID is required") private final int serviceId;
}
