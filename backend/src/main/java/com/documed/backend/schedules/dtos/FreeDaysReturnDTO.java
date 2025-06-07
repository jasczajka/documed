package com.documed.backend.schedules.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class FreeDaysReturnDTO {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private int id;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private LocalDate startDate;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private LocalDate endDate;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private int userId;
}
