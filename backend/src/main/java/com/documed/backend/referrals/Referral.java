package com.documed.backend.referrals;

import com.documed.backend.visits.Visit;

import java.util.Date;

public class Referral {
    private int id;
    private String diagnosis;
    private ReferralType type;
    private Visit visit;
    private Date expirationDate;

}
