package com.documed.backend.users.services;

import com.documed.backend.exceptions.BadRequestException;
import com.documed.backend.exceptions.NotFoundException;
import com.documed.backend.services.ServiceDAO;
import com.documed.backend.users.SubscriptionDAO;
import com.documed.backend.users.SubscriptionToServiceDAO;
import com.documed.backend.users.model.Subscription;
import com.documed.backend.users.model.SubscriptionToService;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SubscriptionService {

  private final SubscriptionDAO subscriptionDAO;
  private final SubscriptionToServiceDAO subscriptionToServiceDAO;
  private final ServiceDAO serviceDAO;

  public Subscription getById(int id) {
    return subscriptionDAO
        .getById(id)
        .orElseThrow(() -> new NotFoundException("Subscription not found"));
  }

  public List<Subscription> getAll() {
    return subscriptionDAO.getAll();
  }

  public Subscription createSubscription(String name, BigDecimal price) {
    Subscription subscriptionToCreate = Subscription.builder().name(name).price(price).build();
    Subscription createdSubscription = subscriptionDAO.create(subscriptionToCreate);

    createSubscriptionToServiceForNewSubscription(createdSubscription.getId());

    return createdSubscription;
  }

  public List<SubscriptionToService> getAllSubscriptionToServiceForSubscription(
      int subscriptionId) {
    return subscriptionToServiceDAO.getForSubscription(subscriptionId);
  }

  public int getDiscountForService(int serviceId, int subscriptionId) {
    return subscriptionToServiceDAO.getDiscountForService(serviceId, subscriptionId);
  }

  public void createSubscriptionToService(SubscriptionToService subscriptionToService) {
    subscriptionToServiceDAO.create(subscriptionToService);
  }

  public void updateSubscriptionToService(int serviceId, int subscriptionId, int discount) {
    if (discount < 0 || discount > 100) {
      throw new BadRequestException("Discount must be between 0 and 100");
    }
    subscriptionToServiceDAO.update(new SubscriptionToService(serviceId, subscriptionId, discount));
  }

  public void createSubscriptionToServiceForNewService(int serviceId) {
    List<Subscription> subscriptions = getAll();
    subscriptions.forEach(
        subscription ->
            createSubscriptionToService(
                new SubscriptionToService(subscription.getId(), serviceId, 0)));
  }

  void createSubscriptionToServiceForNewSubscription(int subscriptionId) {
    List<com.documed.backend.services.model.Service> services = serviceDAO.getAllRegular();

    services.forEach(
        service ->
            createSubscriptionToService(
                new SubscriptionToService(subscriptionId, service.getId(), 0)));
  }

  public void deleteSubscriptionToServiceForService(int serviceId) {
    subscriptionToServiceDAO.deleteForService(serviceId);
  }

  void deleteSubscriptionToServiceForSubscription(int subscriptionId) {
    subscriptionToServiceDAO.deleteForSubscription(subscriptionId);
  }

  @Transactional
  public void deleteSubscription(int subscriptionId) {
    deleteSubscriptionToServiceForSubscription(subscriptionId);
    subscriptionDAO.delete(subscriptionId);
  }

  public List<SubscriptionToService> getAllServiceSubscriptionDiscounts() {
    return subscriptionToServiceDAO.getAll();
  }
}
