package com.documed.backend.prescriptions;

import com.documed.backend.visits.Visit;
import java.util.Date;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class Prescription {
  private int id;
  private int accessCode;
  private String description;
  private final Date date;
  private Date expirationDate;
  private String pesel;
  private String passportNumber;
  private PrescriptionStatus status;
  @NonNull private Visit visit;

}
