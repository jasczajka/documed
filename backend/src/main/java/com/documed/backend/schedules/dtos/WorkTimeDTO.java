package com.documed.backend.schedules.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import lombok.Data;

@Data
public class WorkTimeDTO {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "Day of week is required") private DayOfWeek dayOfWeek;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = " Start Time is required") private LocalTime startTime;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "End Time is required") private LocalTime endTime;
}
