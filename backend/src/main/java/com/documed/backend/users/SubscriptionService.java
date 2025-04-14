package com.documed.backend.users;

import com.documed.backend.services.Service;
import com.documed.backend.users.model.Subscription;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SubscriptionService {
  private Subscription subscription;
  private Service service;
  private int discount;
}
