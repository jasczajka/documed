package com.documed.backend.schedules.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import lombok.Data;

@Data
public class WorkTimeDTO {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "Należy podać dzień tygodnia") private DayOfWeek dayOfWeek;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "Czas rozpoczęcia jest wymagany") private LocalTime startTime;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "Czas zakończenia jest wymagany") private LocalTime endTime;
}
