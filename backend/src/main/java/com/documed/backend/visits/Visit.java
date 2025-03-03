package com.documed.backend.visits;

import com.documed.backend.attachments.Attachment;
import com.documed.backend.notifications.Notification;
import com.documed.backend.others.Facility;
import com.documed.backend.others.Feedback;
import com.documed.backend.prescription.Prescription;
import com.documed.backend.referrals.Referral;
import com.documed.backend.schedules.TimeSlot;
import com.documed.backend.services.Service;
import com.documed.backend.users.User;

import java.util.List;

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
    private Feedback feedback;
    private Attachment attachment;
    private List<Prescription> prescriptions;
    private List<Referral> referrals;
    private List<Notification> notifications;
    private List<TimeSlot> timeSlots;
}
