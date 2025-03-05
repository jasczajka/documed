package com.documed.backend.users;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import lombok.NonNull;

@Data
public class Subscription {

  private final int id;
  @NonNull private String name;
  @NonNull private BigDecimal price;
  private List<User> users;
  private List<SubscriptionService> subscriptionServices;
}
