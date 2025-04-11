package com.documed.backend.prescriptions;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PrescriptionService {

    private final PrescriptionDAO prescriptionDAO;


    Prescription createPrescription(Prescription prescription) {
        return prescriptionDAO.create(prescription);
    }

    List<Prescription> getAllPrescriptionsForVisit(int visitId) {
        return prescriptionDAO.getAllPrescriptionsForVisit(visitId);
    }

    int removePrescriptionForVisit(int prescriptionId) {
        return prescriptionDAO.removePrescriptionFromVisit(prescriptionId);
    }

}
