package com.documed.backend.visits.model;

import com.documed.backend.attachments.model.Attachment;
import com.documed.backend.notifications.Notification;
import com.documed.backend.prescriptions.Prescription;
import com.documed.backend.referrals.Referral;
import com.documed.backend.services.Service;
import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class Visit {
  private int id;
  @NonNull private VisitStatus status;
  private String interview;
  private String diagnosis;
  private String recommendations;
  private BigDecimal totalCost;
  @NonNull private int facilityId;
  @NonNull private int serviceId;
  private String patientInformation;
  @NonNull private int patientId;
  @NonNull private int doctorId;
  private int prescriptionId;
  private Feedback feedback;
  private List<Attachment> attachments;
  private List<Referral> referrals;
  private List<Notification> notifications;
}
