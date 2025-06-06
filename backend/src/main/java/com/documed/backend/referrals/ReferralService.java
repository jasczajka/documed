package com.documed.backend.referrals;

import com.documed.backend.exceptions.NotFoundException;
import com.documed.backend.referrals.dtos.CreateReferralDTO;
import com.documed.backend.referrals.model.Referral;
import com.documed.backend.referrals.model.ReferralStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReferralService {

  private final ReferralDAO referralDAO;

  public Referral createReferral(CreateReferralDTO createReferralDTO) {
    return referralDAO.create(createReferralDTO);
  }

  public Referral getReferralById(int referralId) {
    return referralDAO
        .getById(referralId)
        .orElseThrow(() -> new NotFoundException("Referral not found"));
  }

  public List<Referral> getReferralsForVisit(int visitId) {
    return referralDAO.getReferralsForVisit(visitId);
  }

  public List<Referral> getReferralsForPatient(int patientId) {
    return referralDAO.getReferralsForPatient(patientId);
  }

  public int deleteReferral(int referralId) {
    return referralDAO.delete(referralId);
  }

  public Integer getUserIdForReferralById(int referralId) {
    Integer userId = referralDAO.getUserIdForReferralById(referralId);
    if (userId == null) {
      throw new NotFoundException("User or referral not found");
    } else {
      return userId;
    }
  }

  public void issueReferral(int referralId) {
    referralDAO.updateReferralStatus(referralId, ReferralStatus.ISSUED);
  }
}
