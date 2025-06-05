package com.documed.backend.visits.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GiveFeedbackDTO {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "Rating is required") @Min(value = 1, message = "Rating must be at least 1") @Max(value = 5, message = "Rating must be at most 5") private final int rating;

  private final String message;
}
