package com.documed.backend.referrals.model;

import com.documed.backend.referrals.dtos.ReturnReferralDTO;

public class ReferralMapper {

  public static ReturnReferralDTO toDTO(Referral referral) {
    return ReturnReferralDTO.builder()
        .id(referral.getId())
        .diagnosis(referral.getDiagnosis())
        .type(referral.getType())
        .visitId(referral.getVisitId())
        .expirationDate(referral.getExpirationDate())
        .status(referral.getStatus())
        .build();
  }
}
