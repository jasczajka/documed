package com.documed.backend.referrals.dtos;

import com.documed.backend.referrals.model.ReferralType;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReturnReferralDTO {
  private int id;
  private String diagnosis;
  private ReferralType type;
  private int visitId;
  private LocalDate expirationDate;
}
