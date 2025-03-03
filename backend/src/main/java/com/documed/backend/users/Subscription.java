package com.documed.backend.users;

import com.documed.backend.services.Service;

import java.util.List;
import java.util.Set;

public class Subscription {

    private int id;
    private String name;
    private float price;
    private List<User> users;
    private List<SubscriptionService> subscriptionServices;
}
