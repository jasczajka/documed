package com.documed.backend.auth.dtos;

import com.documed.backend.auth.model.OtpPurpose;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OtpGenerationResponse {
  private String email;
  private OtpPurpose purpose;
  private LocalDateTime expiresAt;
  private String message;
}
