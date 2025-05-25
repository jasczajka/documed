package com.documed.backend.users.services;

import com.documed.backend.exceptions.NotFoundException;
import com.documed.backend.services.ServiceService;
import com.documed.backend.users.SubscriptionDAO;
import com.documed.backend.users.SubscriptionToServiceDAO;
import com.documed.backend.users.model.Subscription;
import java.util.List;

import com.documed.backend.users.model.SubscriptionToService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SubscriptionService {

  private final SubscriptionDAO subscriptionDAO;
  private final SubscriptionToServiceDAO subscriptionToServiceDAO;
  private final ServiceService serviceService;

  public Subscription getById(int id) {
    return subscriptionDAO
        .getById(id)
        .orElseThrow(() -> new NotFoundException("Subscription not found"));
  }

  public List<Subscription> getAll() {
    return subscriptionDAO.getAll();
  }

  public List<SubscriptionToService> getAllSubscriptionToServiceForSubscription(int subscriptionId) {
    return subscriptionToServiceDAO.getForSubscription(subscriptionId);
  }

  public int getDiscountForService(int serviceId, int subscriptionId) {
    return subscriptionToServiceDAO.getDiscountForService(serviceId, subscriptionId);
  }

  public void createSubscriptionToService(SubscriptionToService subscriptionToService) {
    subscriptionToServiceDAO.create(subscriptionToService);
  }

  public void updateSubscriptionToService(SubscriptionToService subscriptionToService) {
    subscriptionToServiceDAO.update(subscriptionToService);
  }

  public void createSubscriptionToServiceForNewService(int serviceId){
    List<Subscription> subscriptions = getAll();
    subscriptions.forEach(subscription ->
            createSubscriptionToService(new SubscriptionToService(serviceId, subscription.getId(), 0)));
  }

  void createSubscriptionToServiceForNewSubscription(int subscriptionId) {
    List<com.documed.backend.services.model.Service> services = serviceService.getAll();

    services.forEach(service ->
            createSubscriptionToService(new SubscriptionToService(service.getId(), subscriptionId, 0)));

  }



}
