package com.documed.backend.prescriptions;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Prescription {
  private int id;
  private int accessCode;
  private final Date date;
  private Date expirationDate;
  private PrescriptionStatus status;

}
