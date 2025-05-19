package com.documed.backend.additionalservices.model;

import com.documed.backend.notifications.Notification;
import com.documed.backend.services.model.Service;
import com.documed.backend.users.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class AdditionalService {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private int id;

  private String description;

  @NonNull private Date date;

  @NonNull private User fulfiller;

  @NonNull private User patient;

  @NonNull private Service service;

  private List<Notification> notifications;

  private List<String> attachmentUrls;
}
