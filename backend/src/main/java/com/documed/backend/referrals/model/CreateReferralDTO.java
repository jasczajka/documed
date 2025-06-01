package com.documed.backend.referrals.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import lombok.Data;
import lombok.NonNull;

@Data
public class CreateReferralDTO {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private int visitId;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private ReferralType type;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank private String diagnosis;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @Future(message = "Expiration date cannot be from the past") @NonNull private LocalDate expirationDate;
}
