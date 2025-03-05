package com.documed.backend.prescription;

import com.documed.backend.visits.Visit;
import lombok.Data;
import lombok.NonNull;

import java.util.Date;

@Data
public class Prescription {
    private final int id;
    private int accessCode;
    private String description;
    private final Date date;
    @NonNull
    private Date expirationDate;
    private int pesel;
    private String passportNumber;
    @NonNull
    private Visit visit;
}
