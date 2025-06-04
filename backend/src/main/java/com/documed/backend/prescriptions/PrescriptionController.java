package com.documed.backend.prescriptions;

import com.documed.backend.auth.AuthService;
import com.documed.backend.auth.annotations.StaffOnly;
import com.documed.backend.auth.annotations.StaffOnlyOrSelf;
import com.documed.backend.auth.exceptions.UnauthorizedException;
import com.documed.backend.medicines.model.Medicine;
import com.documed.backend.medicines.model.MedicineWithAmount;
import com.documed.backend.prescriptions.dtos.CreatePrescriptionDTO;
import com.documed.backend.prescriptions.model.Prescription;
import com.documed.backend.users.model.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

  private final PrescriptionService prescriptionService;
  private final AuthService authService;

  @StaffOnly
  @PostMapping("/visit/{visit_id}")
  @Operation(summary = "Create Prescription")
  public ResponseEntity<Prescription> createPrescription(
      @PathVariable("visit_id") int visitId,
      @Valid @RequestBody CreatePrescriptionDTO createPrescriptionDTO) {
    return new ResponseEntity<>(
        prescriptionService.createPrescription(visitId, createPrescriptionDTO.getExpirationDate()),
        HttpStatus.OK);
  }

  @GetMapping("/visit/{visit_id}")
  @Operation(summary = "Get Prescription For Visit")
  public ResponseEntity<Optional<Prescription>> getPrescriptionForVisit(
      @PathVariable("visit_id") int visitId) {
    Optional<Prescription> prescription = prescriptionService.getPrescriptionForVisit(visitId);
    if (prescription.isPresent()) {
      int userId = authService.getCurrentUserId();
      int prescriptionUserId =
          prescriptionService.getUserIdForPrescriptionById(prescription.get().getId());
      UserRole userRole = authService.getCurrentUserRole();
      if (userRole == UserRole.PATIENT && prescriptionUserId != userId) {
        throw new UnauthorizedException("This patient has no access to this prescription");
      }
    }

    return ResponseEntity.ok(prescription);
  }

  @StaffOnlyOrSelf
  @GetMapping("/user/{user_id}")
  public ResponseEntity<List<Prescription>> getPrescriptionsForUser(
      @PathVariable("user_id") int userId) {
    List<Prescription> prescriptions = prescriptionService.getPrescriptionsForUser(userId);
    return ResponseEntity.ok(prescriptions);
  }

  @GetMapping("/{prescription_id}/medicines")
  public ResponseEntity<List<MedicineWithAmount>> getMedicinesForPrescription(
      @PathVariable("prescription_id") int prescriptionId) {

    int userId = authService.getCurrentUserId();
    int prescriptionUserId = prescriptionService.getUserIdForPrescriptionById(prescriptionId);
    UserRole userRole = authService.getCurrentUserRole();

    if (userRole.equals(UserRole.PATIENT) && userId != prescriptionUserId) {
      throw new UnauthorizedException("Requesting patient id and prescription id do not match");
    }

    List<MedicineWithAmount> medicines =
        prescriptionService.getMedicinesForPrescription(prescriptionId);
    return ResponseEntity.ok(medicines);
  }

  @StaffOnly
  @GetMapping
  public List<Prescription> getAllPrescriptions() {
    return prescriptionService.getAll();
  }

  @StaffOnly
  @DeleteMapping("/{prescription_id}")
  @Operation(summary = "Remove prescription")
  public ResponseEntity<Integer> removePrescription(
      @PathVariable("prescription_id") int prescriptionId) {
    int result = prescriptionService.removePrescription(prescriptionId);
    return ResponseEntity.ok(result);
  }

  @StaffOnly
  @PostMapping("/{prescription_id}/medicine/{medicine_id}")
  @Operation(summary = "Add Medicine To Prescription")
  public ResponseEntity<Medicine> addMedicineToPrescription(
      @PathVariable("prescription_id") int prescriptionId,
      @PathVariable("medicine_id") String medicineId,
      @RequestParam(defaultValue = "1") int amount) {
    return prescriptionService
        .addMedicineToPrescription(prescriptionId, medicineId, amount)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @StaffOnly
  @DeleteMapping("/{prescription_id}/medicine/{medicine_id}")
  @Operation(summary = "Remove Medicine From Prescription")
  public ResponseEntity<Integer> removeMedicineFromPrescription(
      @PathVariable("prescription_id") int prescriptionId,
      @PathVariable("medicine_id") String medicineId) {
    int result = prescriptionService.removeMedicineFromPrescription(prescriptionId, medicineId);
    return ResponseEntity.ok(result);
  }

  @StaffOnly
  @PatchMapping("/{prescription_id}/issue")
  @Operation(summary = "Issue prescription")
  public ResponseEntity<Prescription> issuePrescription(
      @PathVariable("prescription_id") int prescriptionId,
      @RequestBody LocalDate newExpirationDate) {
    return ResponseEntity.ok(
        prescriptionService.issuePrescription(prescriptionId, newExpirationDate));
  }

  @StaffOnly
  @PatchMapping("/{prescription_id}/expiration-date")
  @Operation(summary = "Update prescription expiration Date")
  public ResponseEntity<Prescription> updatePrescriptionExpirationDate(
      @PathVariable("prescription_id") int prescriptionId,
      @RequestBody LocalDate newExpirationDate) {
    return ResponseEntity.ok(
        prescriptionService.updatePrescriptionExpirationDate(prescriptionId, newExpirationDate));
  }
}
