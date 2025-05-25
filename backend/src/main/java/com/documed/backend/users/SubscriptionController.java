package com.documed.backend.users;

import com.documed.backend.auth.annotations.StaffOnly;
import com.documed.backend.users.model.Subscription;
import com.documed.backend.users.model.SubscriptionToService;
import com.documed.backend.users.services.SubscriptionService;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

  private final SubscriptionService subscriptionService;

  @StaffOnly
  @GetMapping("/{id}")
  @Operation(summary = "Get subscription by ID")
  public ResponseEntity<Subscription> getSubscription(@PathVariable int id) {
    Subscription subscription = subscriptionService.getById(id);
    return new ResponseEntity<>(subscription, HttpStatus.OK);
  }

  @StaffOnly
  @GetMapping
  @Operation(summary = "Get all subscriptions")
  public ResponseEntity<Iterable<Subscription>> getAllSubscriptions() {
    List<Subscription> subscriptions = subscriptionService.getAll();
    return new ResponseEntity<>(subscriptions, HttpStatus.OK);
  }

  @GetMapping("{subscriptionId}/services")
  public ResponseEntity<List<SubscriptionToService>> getAllSubscriptionToServiceForSubscription(
          @PathVariable int subscriptionId
  ) {
    List<SubscriptionToService> subscriptionToServiceList = subscriptionService.getAllSubscriptionToServiceForSubscription(subscriptionId);
    if (subscriptionToServiceList.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      return new ResponseEntity<>(subscriptionToServiceList, HttpStatus.OK);
    }
  }

}
