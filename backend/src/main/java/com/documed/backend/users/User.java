package com.documed.backend.users;

import com.documed.backend.additionalservices.AdditionalService;
import com.documed.backend.schedules.FreeDay;
import com.documed.backend.schedules.TimeSlot;
import com.documed.backend.schedules.WorkTime;
import com.documed.backend.visits.Visit;

import java.util.Date;
import java.util.List;

public class User {

    private int id;
    private String firstName;
    private String lastName;
    private String pesel;
    private String passportNumber;
    private String email;
    private String address;
    private String password;
    private String phoneNumber;
    private String status;
    private Date birthDate;
    private String pwzNumber;
    private UserRole role;
    private Subscription subscription;
    private List<Specialisation> specialisations;
    private List<TimeSlot> timeSlots;
    private List<WorkTime> workTimes;
    private List<FreeDay> freeDays;
    private List<Visit> visits;
    private List<AdditionalService> additionalServices;

}
