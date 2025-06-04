package com.documed.backend.schedules.dtos;

import com.documed.backend.schedules.model.DayOfWeekEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
public class WorkTimeReturnDTO {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private int id;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private final int userId;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private final int facilityId;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private final DayOfWeekEnum dayOfWeek;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private LocalTime startTime;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private LocalTime endTime;
}
