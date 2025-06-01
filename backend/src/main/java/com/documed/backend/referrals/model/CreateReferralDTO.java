package com.documed.backend.referrals.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateReferralDTO {
    int visitId;
    ReferralType type;
    String diagnosis;
    LocalDate expirationDate;
}
