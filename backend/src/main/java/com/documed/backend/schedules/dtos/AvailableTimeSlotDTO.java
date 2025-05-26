package com.documed.backend.schedules.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AvailableTimeSlotDTO {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private int id;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private final int doctorId;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private final Date startTime;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private boolean isBusy;
}
