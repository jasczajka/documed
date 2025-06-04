package com.documed.backend.referrals.dtos;

import com.documed.backend.referrals.model.ReferralType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReferralTypeDTO {
  @Schema(example = "TO_SPECIALIST")
  private ReferralType code;

  @Schema(example = "Skierowanie do specjalisty")
  private String description;
}
