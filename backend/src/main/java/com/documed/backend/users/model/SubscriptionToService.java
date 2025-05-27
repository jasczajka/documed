package com.documed.backend.users.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SubscriptionToService {
  private final int subscriptionId;
  private final int serviceId;
  private int discount;
}
