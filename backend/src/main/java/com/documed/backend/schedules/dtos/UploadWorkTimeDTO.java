package com.documed.backend.schedules.dtos;

import com.documed.backend.schedules.model.DayOfWeekEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import lombok.*;

@Data
public class UploadWorkTimeDTO {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "Day of week is required") private DayOfWeekEnum dayOfWeek;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = " Start Time is required") private LocalTime startTime;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "End Time is required") private LocalTime endTime;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "Facility is required") private int facilityId;
}
