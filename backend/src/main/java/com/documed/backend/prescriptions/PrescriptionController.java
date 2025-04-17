package com.documed.backend.prescriptions;

import com.documed.backend.auth.annotations.StaffOnly;
import com.documed.backend.auth.annotations.StaffOnlyOrSelf;
import com.documed.backend.medicines.model.Medicine;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

  private final PrescriptionService prescriptionService;

  @StaffOnly
  @PostMapping("/visit/{visit_id}")
  @Operation(summary = "Create Prescription")
  public ResponseEntity<Prescription> createPrescription(@PathVariable("visit_id") int visitId) {
    return new ResponseEntity<>(prescriptionService.createPrescription(visitId), HttpStatus.OK);
  }

  @StaffOnly
  @GetMapping("/visit/{visit_id}")
  @Operation(summary = "Get Prescription For Visit")
  public ResponseEntity<Prescription> getPrescriptionForVisit(
      @PathVariable("visit_id") int visitId) {
    Prescription prescription = prescriptionService.getPrescriptionForVisit(visitId);
    return ResponseEntity.ok(prescription);
  }

  @StaffOnlyOrSelf
  @GetMapping("/user/{user_id}")
  public ResponseEntity<List<Prescription>> getPrescriptionsForUser(
      @PathVariable("user_id") int userId) {
    List<Prescription> prescriptions = prescriptionService.getPrescriptionsForUser(userId);
    return ResponseEntity.ok(prescriptions);
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
  @PatchMapping("/{prescription_id}")
  @Operation(summary = "Issue prescription")
  public ResponseEntity<Prescription> issuePrescription(
      @PathVariable("prescription_id") int prescriptionId) {
    return ResponseEntity.ok(prescriptionService.issuePrescription(prescriptionId));
  }
}
