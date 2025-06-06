package com.documed.backend.referrals.dtos;

import com.documed.backend.referrals.model.ReferralStatus;
import com.documed.backend.referrals.model.ReferralType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class ReturnReferralDTO {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private int id;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String issuingDoctorFullName;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String diagnosis;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private ReferralType type;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private int visitId;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private LocalDate expirationDate;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private ReferralStatus status;
}
