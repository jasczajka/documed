package com.documed.backend.auth.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class Otp {
  private Long id;

  @NonNull private String email;

  @NonNull private String otp;

  @NonNull private OtpPurpose purpose;

  @NonNull private LocalDateTime generatedAt;

  @NonNull private LocalDateTime expiresAt;

  @Builder.Default private int attempts = 0;

  @Builder.Default private boolean used = false;
}
