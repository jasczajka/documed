package com.documed.backend.additionalservices;

import com.documed.backend.attachments.Attachment;
import com.documed.backend.notifications.Notification;
import com.documed.backend.services.Service;
import com.documed.backend.users.User;
import com.documed.backend.visits.VisitStatus;

import java.util.Date;
import java.util.List;

public class AdditionalService {
    private int id;
    private String description;
    private Date date;
    private User performer; //TODO name???
    private Service service;
    private VisitStatus status;
    private List<Notification> notifications;
    private List<Attachment> attachments;
}
