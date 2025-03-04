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
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
public class Visit {
    private final int id;
    @NonNull
    private VisitStatus status;
    private String interview;
    private String diagnosis;
    private String recommendations;
    private float totalCost;
    @NonNull
    private Facility facility;
    @NonNull
    private Service service;
    private String patientInformation;
    @NonNull
    private User patient;
    @NonNull
    private User doctor;
    private Feedback feedback;
    private Attachment attachment;
    private List<Prescription> prescriptions;
    private List<Referral> referrals;
    private List<Notification> notifications;
    @NonNull
    private List<TimeSlot> timeSlots;
}
