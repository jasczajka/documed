package com.documed.backend.visits.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScheduleVisitDTO {
  private final String patientInformation;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "Patient ID is required") private final int patientId;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "Doctor ID is required") private final int doctorId;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "Time slot ID is required") private final int firstTimeSlotId;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "Service ID is required") private final int serviceId;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "Facility ID is required") private final int facilityId;
}
