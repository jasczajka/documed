package com.documed.backend.referrals.model;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class Referral {
  private int id;
  private String diagnosis;
  @NonNull private ReferralType type;
  private final int visitId;
  private LocalDate expirationDate;
}
