package com.documed.backend.referrals;

import com.documed.backend.exceptions.NotFoundException;
import com.documed.backend.referrals.model.CreateReferralDTO;
import com.documed.backend.referrals.model.Referral;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReferralService {

    private final ReferralDAO referralDAO;

    public Referral createReferral(CreateReferralDTO createReferralDTO) {
        return referralDAO.create(createReferralDTO);
    }

    public Referral getReferralById(int referralId) {
        return referralDAO.getById(referralId).orElseThrow(() -> new NotFoundException("Referral not found"));
    }

    public List<Referral> getAllReferrals() {
        return referralDAO.getAll();
    }

    public List<Referral> getReferralsForVisit(int visitId) {
        return referralDAO.getReferralsForVisit(visitId);
    }

    public List<Referral> getReferralsForPatient(int patientId) {
        return referralDAO.getReferralsForPatient(patientId);
    }
}
