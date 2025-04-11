package com.documed.backend.prescriptions;

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
  private Date expirationDate;
  private String pesel;
  private String passportNumber;
  @NonNull private Visit visit;

  public Prescription(int id, int accessCode, String description, Date date, Date expirationDate, String pesel, String passportNumber) {
    this.id = id;
    this.accessCode = accessCode;
    this.description = description;
    this.date = date;
    this.expirationDate = expirationDate;
    this.pesel = pesel;
    this.passportNumber = passportNumber;
  }

  public Prescription(int accessCode, String description, Date date, @NonNull Date expirationDate, String pesel, String passportNumber) {
    this.accessCode = accessCode;
    this.description = description;
    this.date = date;
    this.expirationDate = expirationDate;
    this.pesel = pesel;
    this.passportNumber = passportNumber;
  }

}
