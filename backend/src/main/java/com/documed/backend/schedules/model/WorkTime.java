package com.documed.backend.schedules.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.DayOfWeek;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class WorkTime {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private int id;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private final int userId;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private final DayOfWeek dayOfWeek;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private LocalTime startTime;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private LocalTime endTime;
}
