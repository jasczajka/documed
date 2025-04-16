package com.documed.backend.users.model;

import com.documed.backend.services.Service;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SubscriptionService {
  private Subscription subscription;
  private Service service;
  private int discount;
}
