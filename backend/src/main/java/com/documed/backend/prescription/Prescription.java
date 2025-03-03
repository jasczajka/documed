package com.documed.backend.prescription;

import com.documed.backend.visits.Visit;

import java.util.Date;

public class Prescription {
    int id;
    int accessCode;
    String description;
    Date date;
    int pesel;
    String passportNumber;
    Visit visit;
}
