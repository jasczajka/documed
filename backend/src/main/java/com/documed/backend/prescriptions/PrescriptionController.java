package com.documed.backend.prescriptions;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

//    @PostMapping
//    public ResponseEntity<Prescription> createPrescription(@RequestBody Prescription prescription) {
//        try{
//            Prescription createdPrescription =
//                    prescriptionService.createPrescription(
//                            prescription.get
//                    );
//
//        }
//    }

    @GetMapping("/visit/{visit_id}")
    public ResponseEntity<List<Prescription>> getAllPrescriptionsForVisit(@PathVariable("visit_id") int visitId) {
        List<Prescription> prescriptions =  prescriptionService.getAllPrescriptionsForVisit(visitId);

        if (prescriptions.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(prescriptions);
        }
    }

}
