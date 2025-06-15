package com.documed.backend.notifications.model;

public interface EmailMessageTemplate {
  String getSubject();

  String getBody();

  String getRecipient();

  String getFrom();
}
