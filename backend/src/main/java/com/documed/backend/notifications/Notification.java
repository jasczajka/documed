package com.documed.backend.notifications;

import com.documed.backend.additionalservices.AdditionalService;
import com.documed.backend.visits.Visit;

public class Notification {
    private int id;
    private NotificationStatus status;
    private Visit visit;
    private AdditionalService additionalService;
}
