package com.documed.backend.notifications;

import com.documed.backend.additionalservices.AdditionalService;
import com.documed.backend.visits.model.Visit;
import lombok.Data;
import lombok.NonNull;

@Data
public class Notification {
  private int id;
  @NonNull private NotificationStatus status;
  private Visit visit;
  private AdditionalService additionalService;
  private NotificationType type;
}
