package com.documed.backend.referrals;

import com.documed.backend.auth.AuthService;
import com.documed.backend.auth.annotations.StaffOnly;
import com.documed.backend.auth.annotations.StaffOnlyOrSelf;
import com.documed.backend.referrals.model.CreateReferralDTO;
import com.documed.backend.referrals.model.Referral;
import com.documed.backend.referrals.model.ReferralType;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
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
  @PostMapping
  @Operation(summary = "Create referral")
  public ResponseEntity<Referral> createReferral(
      @RequestBody @Valid CreateReferralDTO createReferralDTO) {
    Referral createdReferral = referralService.createReferral(createReferralDTO);
    return new ResponseEntity<>(createdReferral, HttpStatus.CREATED);
  }

  @StaffOnlyOrSelf
  @GetMapping("/{referralId}")
  @Operation(summary = "Get referral by id")
  public ResponseEntity<Referral> getReferralById(@PathVariable int referralId) {
    Referral referral = referralService.getReferralById(referralId);

    return new ResponseEntity<>(referral, HttpStatus.OK);
  }

  @StaffOnlyOrSelf
  @GetMapping("/patient/{patientId}")
  @Operation(summary = "Get all referrals for patient")
  public ResponseEntity<List<Referral>> getAllReferralsForPatient(@PathVariable int patientId) {
    List<Referral> referrals = referralService.getReferralsForPatient(patientId);
    return new ResponseEntity<>(referrals, HttpStatus.OK);
  }

  @StaffOnlyOrSelf
  @GetMapping("/visit/{visitId}")
  @Operation(summary = "Get all referrals for visit")
  public ResponseEntity<List<Referral>> getAllReferralsForVisit(@PathVariable int visitId) {
    List<Referral> referrals = referralService.getReferralsForVisit(visitId);
    return new ResponseEntity<>(referrals, HttpStatus.OK);
  }

  @StaffOnly
  @DeleteMapping("/{referralId}")
  @Operation(summary = "Remove referral")
  public ResponseEntity<Integer> removeReferral(@PathVariable int referralId) {
    int result = referralService.deleteReferral(referralId);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @StaffOnly
  @GetMapping("/types")
  @Operation(summary = "Get all referral types")
  public ResponseEntity<ReferralType[]> getAllReferralTypes() {
    return ResponseEntity.ok(ReferralType.values());
  }
}
