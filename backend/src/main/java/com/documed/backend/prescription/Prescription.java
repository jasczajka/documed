package com.documed.backend.prescription;

import com.documed.backend.visits.Visit;
import java.util.Date;
import lombok.Data;
import lombok.NonNull;

@Data
public class Prescription {
  private int id;
  private int accessCode;
  private String description;
  private final Date date;
  @NonNull private Date expirationDate;
  private int pesel;
  private String passportNumber;
  @NonNull private Visit visit;
}
