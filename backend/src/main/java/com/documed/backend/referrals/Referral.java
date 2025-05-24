package com.documed.backend.referrals;

import com.documed.backend.visits.model.Visit;
import java.util.Date;
import lombok.Data;
import lombok.NonNull;

@Data
public class Referral {
  private int id;
  private String diagnosis;
  @NonNull private ReferralType type;
  private final Visit visit;
  private Date expirationDate;
}
