package com.documed.backend.notifications;

import com.documed.backend.additionalservices.AdditionalService;
import com.documed.backend.visits.Visit;
import lombok.Data;
import lombok.NonNull;

@Data
public class Notification {
    private final int id;
    @NonNull
    private NotificationStatus status;
    private Visit visit;
    private AdditionalService additionalService;
}
