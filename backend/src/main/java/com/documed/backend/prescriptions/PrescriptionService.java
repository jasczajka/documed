package com.documed.backend.prescriptions;

import com.documed.backend.medicines.MedicineDAO;
import com.documed.backend.medicines.model.Medicine;
import com.documed.backend.medicines.model.MedicineWithAmount;
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

  Prescription getPrescriptionForVisit(int visitId) {
    return prescriptionDAO.getPrescriptionForVisit(visitId);
  }

  List<Prescription> getPrescriptionsForUser(int userId) {
    return prescriptionDAO.getPrescriptionsForUser(userId);
  }

  List<MedicineWithAmount> getMedicinesForPrescription(int prescriptionId) {
    return medicineDAO.getForPrescription(prescriptionId);
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

  public Integer getUserIdForPrescriptionById(int prescriptionId) {
    return prescriptionDAO.getUserIdForPrescriptionById(prescriptionId);
  }
}
