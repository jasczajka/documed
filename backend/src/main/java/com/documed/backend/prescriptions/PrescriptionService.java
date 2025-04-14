package com.documed.backend.prescriptions;

import com.documed.backend.medicines.model.Medicine;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PrescriptionService {

    private final PrescriptionDAO prescriptionDAO;


    Prescription createPrescription(int visitId, String pesel, String passportNumber) {
        return prescriptionDAO.create(visitId, pesel, passportNumber);
    }

    Prescription getPrescriptionForVisit(int visitId) {
        return prescriptionDAO.getPrescriptionForVisit(visitId);
    }

    List<Prescription> getAllPrescriptionsForUser(int userId) {
        return prescriptionDAO.getAllPrescriptionsForUser(userId);
    }

    Optional<Medicine> addMedicineToPrescription(int prescriptionId, String medicineId, int amount) {
         return prescriptionDAO.addMedicineToPrescription(prescriptionId, medicineId, amount);
    }

    int removeMedicineFromPrescription(int prescriptionId, String medicineId) {
        return prescriptionDAO.removeMedicineFromPrescription(prescriptionId, medicineId);
    }

    int removePrescription(int prescriptionId) {
        return prescriptionDAO.delete(prescriptionId);
    }

    Prescription issuePrescription(int prescriptionId) {
        return prescriptionDAO.issuePrescription(prescriptionId);
    }

}
