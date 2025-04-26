package com.documed.backend.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OtpValidationResponse {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private boolean valid;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String message;
}
