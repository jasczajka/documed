package com.documed.backend.additionalservices;

import com.documed.backend.attachments.Attachment;
import com.documed.backend.notifications.Notification;
import com.documed.backend.services.Service;
import com.documed.backend.users.User;
import com.documed.backend.visits.VisitStatus;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.NonNull;

@Data
public class AdditionalService {
  private final int id;
  private String description;
  @NonNull private Date date;
  @NonNull private User fulfiller;
  @NonNull private Service service;
  @NonNull private VisitStatus status;
  private List<Notification> notifications;
  private List<Attachment> attachments;
}
