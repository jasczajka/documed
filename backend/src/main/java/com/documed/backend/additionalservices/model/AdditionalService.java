package com.documed.backend.additionalservices.model;

import com.documed.backend.notifications.Notification;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class AdditionalService {
  private int id;

  private String description;

  @NonNull private LocalDate date;

  private int fulfillerId;

  private int patientId;

  private int serviceId;

  private List<Notification> notifications;

  private List<String> attachmentUrls;
}
