package com.documed.backend.users;

import com.documed.backend.services.Service;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
public class Subscription {

    private final int id;
    @NonNull
    private String name;
    @NonNull
    private BigDecimal price;
    private List<User> users;
    private List<SubscriptionService> subscriptionServices;
}
