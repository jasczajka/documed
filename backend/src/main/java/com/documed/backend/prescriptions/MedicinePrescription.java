package com.documed.backend.prescriptions;

import com.documed.backend.medicines.Medicine;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MedicinePrescription {
  private Medicine medicine;
  private Prescription prescription;
  private int amount;
}
