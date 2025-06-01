package com.documed.backend.referrals;

import com.documed.backend.auth.AuthService;
import com.documed.backend.auth.annotations.StaffOnly;
import com.documed.backend.prescriptions.PrescriptionService;
import com.documed.backend.referrals.model.CreateReferralDTO;
import com.documed.backend.referrals.model.Referral;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/referrals")
@RequiredArgsConstructor
public class ReferralController {

    private final ReferralService referralService;
    private final AuthService authService;

    @StaffOnly
    @PostMapping("/{visitId}")
    public ResponseEntity<Referral> createReferral(CreateReferralDTO createReferralDTO) {
        Referral createdReferral = referralService.createReferral(createReferralDTO);
        return new ResponseEntity<>(createdReferral, HttpStatus.CREATED);
    }

    @GetMapping("/{referralId}")
    public ResponseEntity<Referral> getReferralById(@PathVariable int referralId) {
        Referral referral = referralService.getReferralById(referralId);

        return new ResponseEntity<>(referral, HttpStatus.OK);
    }



}
