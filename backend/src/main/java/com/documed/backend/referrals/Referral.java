package com.documed.backend.referrals;

import com.documed.backend.visits.Visit;
import lombok.Data;
import lombok.NonNull;

import java.util.Date;

@Data
public class Referral {
    private final int id;
    private String diagnosis;
    @NonNull
    private ReferralType type;
    private final Visit visit;
    private Date expirationDate;

}
