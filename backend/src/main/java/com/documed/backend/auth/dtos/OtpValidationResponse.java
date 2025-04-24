package com.documed.backend.auth.dtos;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OtpValidationResponse {
  private boolean valid;
  private String message;
}
