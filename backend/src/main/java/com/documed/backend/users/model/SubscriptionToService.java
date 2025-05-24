package com.documed.backend.users.model;

import com.documed.backend.services.model.Service;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SubscriptionToService {
  private Subscription subscription;
  private Service service;
  private int discount;
}
