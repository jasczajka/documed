package com.documed.backend.prescriptions;

import com.documed.backend.medicines.model.Medicine;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping("/visit/{visit_id}")
    @Operation(summary = "Create Prescription")
    public ResponseEntity<Prescription> createPrescription(@PathVariable("visit_id") int visitId) {
        return ResponseEntity.ok(prescriptionService.createPrescription(visitId, "11111111111", "testValue"));
    }

    @GetMapping("/visit/{visit_id}")
    @Operation(summary = "Get Prescription For Visit")
    public ResponseEntity<Prescription> getPrescriptionForVisit(@PathVariable("visit_id") int visitId) {
        Prescription prescription = prescriptionService.getPrescriptionForVisit(visitId);

        return ResponseEntity.ok(prescription);
    }

    @DeleteMapping("/{prescription_id}")
    @Operation(summary = "Remove prescription")
    public ResponseEntity<Integer> removePrescription(
            @PathVariable("prescription_id") int prescriptionId) {
        int result = prescriptionService.removePrescription(prescriptionId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{prescription_id}/medicine/{medicine_id}")
    @Operation(summary = "Add Medicine To Prescription")
    public ResponseEntity<Medicine> addMedicineToPrescription(
            @PathVariable("prescription_id") int prescriptionId,
            @PathVariable("medicine_id") String medicineId,
            @RequestParam(defaultValue = "1") int amount
    ){
        return prescriptionService.addMedicineToPrescription(prescriptionId, medicineId, amount)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());


    }

    @DeleteMapping("/{prescription_id}/medicine/{medicine_id}")
    @Operation(summary = "Remove Medicine From Prescription")
    public ResponseEntity<Integer> removeMedicineFromPrescription(
            @PathVariable("prescription_id") int prescriptionId,
            @PathVariable("medicine_id") String medicineId
    ){
        int result = prescriptionService.removeMedicineFromPrescription(prescriptionId, medicineId);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{prescription_id}")
    @Operation(summary = "Issue prescription")
    public ResponseEntity<Prescription> issuePrescription(@PathVariable("prescription_id") int prescriptionId) {
        return ResponseEntity.ok(prescriptionService.issuePrescription(prescriptionId));
    }

}
