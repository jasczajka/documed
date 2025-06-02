package com.documed.backend.referrals;

import com.documed.backend.auth.AuthService;
import com.documed.backend.auth.annotations.StaffOnly;
import com.documed.backend.auth.annotations.StaffOnlyOrSelf;
import com.documed.backend.auth.exceptions.UnauthorizedException;
import com.documed.backend.referrals.dtos.CreateReferralDTO;
import com.documed.backend.referrals.dtos.ReturnReferralDTO;
import com.documed.backend.referrals.model.Referral;
import com.documed.backend.referrals.model.ReferralMapper;
import com.documed.backend.referrals.model.ReferralType;
import com.documed.backend.users.model.UserRole;
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
  public ResponseEntity<ReturnReferralDTO> createReferral(
      @RequestBody @Valid CreateReferralDTO createReferralDTO) {
    Referral createdReferral = referralService.createReferral(createReferralDTO);
    ReturnReferralDTO dto = ReferralMapper.toDTO(createdReferral);
    return new ResponseEntity<>(dto, HttpStatus.CREATED);
  }

  @GetMapping("/{referralId}")
  @Operation(summary = "Get referral by id")
  public ResponseEntity<ReturnReferralDTO> getReferralById(@PathVariable int referralId) {

    int userId = authService.getCurrentUserId();
    int prescriptionUserId = referralService.getUserIdForReferralById(referralId);
    UserRole userRole = authService.getCurrentUserRole();

    if (userRole.equals(UserRole.PATIENT) && userId != prescriptionUserId) {
      throw new UnauthorizedException("Requesting patient id and referral id do not match");
    }

    Referral referral = referralService.getReferralById(referralId);
    ReturnReferralDTO dto = ReferralMapper.toDTO(referral);
    return new ResponseEntity<>(dto, HttpStatus.OK);
  }

  @StaffOnlyOrSelf
  @GetMapping("/patient/{userId}")
  @Operation(summary = "Get all referrals for patient")
  public ResponseEntity<List<ReturnReferralDTO>> getAllReferralsForPatient(
      @PathVariable int userId) {
    List<Referral> referrals = referralService.getReferralsForPatient(userId);
    List<ReturnReferralDTO> dtos = referrals.stream().map(ReferralMapper::toDTO).toList();
    return new ResponseEntity<>(dtos, HttpStatus.OK);
  }

  @StaffOnlyOrSelf
  @GetMapping("/visit/{visitId}")
  @Operation(summary = "Get all referrals for visit")
  public ResponseEntity<List<ReturnReferralDTO>> getAllReferralsForVisit(
      @PathVariable int visitId) {
    List<Referral> referrals = referralService.getReferralsForVisit(visitId);
    List<ReturnReferralDTO> dtos = referrals.stream().map(ReferralMapper::toDTO).toList();
    return new ResponseEntity<>(dtos, HttpStatus.OK);
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
