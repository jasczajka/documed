package com.documed.backend.prescriptions;

import com.documed.backend.medicines.MedicineDAO;
import com.documed.backend.medicines.model.Medicine;
import com.documed.backend.medicines.model.MedicineWithAmount;
import com.documed.backend.prescriptions.exceptions.AlreadyIssuedException;
import com.documed.backend.prescriptions.exceptions.WrongAmountException;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PrescriptionService {

  private final PrescriptionDAO prescriptionDAO;
  private final MedicineDAO medicineDAO;

  public List<Prescription> getAll() {
    return prescriptionDAO.getAll();
  }

  public Optional<Prescription> getById(int prescriptionId) {
    return prescriptionDAO.getById(prescriptionId);
  }

  Prescription createPrescription(int visitId) {
    return prescriptionDAO.create(visitId);
  }

  Optional<Prescription> getPrescriptionForVisit(int visitId) {
    return prescriptionDAO.getPrescriptionForVisit(visitId);
  }

  List<Prescription> getPrescriptionsForUser(int userId) {
    return prescriptionDAO.getPrescriptionsForUser(userId);
  }

  List<MedicineWithAmount> getMedicinesForPrescription(int prescriptionId) {
    return medicineDAO.getForPrescription(prescriptionId);
  }

  Optional<Medicine> addMedicineToPrescription(int prescriptionId, String medicineId, int amount) {
    if (amount < 1) {
      throw new WrongAmountException("Amount can't be smaller than 1");
    }
    return prescriptionDAO.addMedicineToPrescription(prescriptionId, medicineId, amount);
  }

  int removeMedicineFromPrescription(int prescriptionId, String medicineId) {
    return prescriptionDAO.removeMedicineFromPrescription(prescriptionId, medicineId);
  }

  int removePrescription(int prescriptionId) {
    return prescriptionDAO.delete(prescriptionId);
  }

  public Prescription issuePrescription(int prescriptionId) {
    Optional<Prescription> existingPrescription = prescriptionDAO.getById(prescriptionId);

    if (existingPrescription.isPresent()
        && existingPrescription.get().getStatus() == PrescriptionStatus.ISSUED) {
      throw new AlreadyIssuedException("Prescription is already issued");
    }

    int rowsAffected =
        prescriptionDAO.updatePrescriptionStatus(prescriptionId, PrescriptionStatus.ISSUED);

    if (rowsAffected > 0) {
      return prescriptionDAO
          .getById(prescriptionId)
          .orElseThrow(() -> new IllegalStateException("Prescription not found after update"));
    } else {
      throw new IllegalStateException("Failed to update prescription");
    }
  }

  public Integer getUserIdForPrescriptionById(int prescriptionId) {
    return prescriptionDAO.getUserIdForPrescriptionById(prescriptionId);
  }
}
