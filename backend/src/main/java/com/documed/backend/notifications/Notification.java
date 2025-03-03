package com.documed.backend.notifications;

import com.documed.backend.additionalservices.AddditionalService;
import com.documed.backend.visits.Visit;

public class Notification {
    private int id;
    private NotificationStatus status;
    private Visit visit;
    private AddditionalService addditionalService;
}
