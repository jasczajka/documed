package com.documed.backend.visits;

import com.documed.backend.others.Facility;
import com.documed.backend.services.Service;
import com.documed.backend.users.User;

public class Visit {
    private int id;
    private VisitStatus status;
    private String interview;
    private String diagnosis;
    private String recommendations;
    private float totalCost;
    private Facility facility;
    private Service service;
    private String patientInformation;
    private User patient;
    private User doctor;
}
