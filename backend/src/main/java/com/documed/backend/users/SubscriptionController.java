package com.documed.backend.users;

import com.documed.backend.auth.annotations.AdminOnly;
import com.documed.backend.auth.annotations.StaffOnly;
import com.documed.backend.users.model.Subscription;
import com.documed.backend.users.model.SubscriptionToService;
import com.documed.backend.users.services.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

  @GetMapping
  @Operation(summary = "Get all subscriptions")
  public ResponseEntity<List<Subscription>> getAllSubscriptions() {
    List<Subscription> subscriptions = subscriptionService.getAll();
    return new ResponseEntity<>(subscriptions, HttpStatus.OK);
  }

  @GetMapping("/discounts")
  @Operation(summary = "Get all service with subscription and associated discount")
  public ResponseEntity<List<SubscriptionToService>> getAllServiceSubscriptionDiscounts() {
    List<SubscriptionToService> subscriptionsToService =
        subscriptionService.getAllServiceSubscriptionDiscounts();
    return new ResponseEntity<>(subscriptionsToService, HttpStatus.OK);
  }

  @GetMapping("{subscriptionId}/services")
  public ResponseEntity<List<SubscriptionToService>> getAllSubscriptionToServiceForSubscription(
      @PathVariable int subscriptionId) {
    List<SubscriptionToService> subscriptionToServiceList =
        subscriptionService.getAllSubscriptionToServiceForSubscription(subscriptionId);
    if (subscriptionToServiceList.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      return new ResponseEntity<>(subscriptionToServiceList, HttpStatus.OK);
    }
  }

  // TODO clerk only
  @StaffOnly
  @PutMapping("{subscriptionId}/services/{serviceId}")
  public ResponseEntity<String> updateServiceDiscount(
      @PathVariable int subscriptionId,
      @PathVariable int serviceId,
      @RequestBody
          @Min(value = 0, message = "Discount cannot be negative") @Max(value = 100, message = "Discount cannot exceed 100%") int discount) {

    subscriptionService.updateSubscriptionToService(serviceId, subscriptionId, discount);
    return new ResponseEntity<>("Service discount updated", HttpStatus.OK);
  }

  @AdminOnly
  @PostMapping
  public ResponseEntity<Subscription> createSubscription(String name, BigDecimal price) {

    if (price.compareTo(BigDecimal.ZERO) <= 0) {
      return ResponseEntity.badRequest().build();
    }

    Subscription subscription = subscriptionService.createSubscription(name, price);
    return new ResponseEntity<>(subscription, HttpStatus.CREATED);
  }

  @AdminOnly
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteSubscription(@PathVariable int id) {
    subscriptionService.deleteSubscription(id);
    return new ResponseEntity<>("Subscription deleted", HttpStatus.OK);
  }
}
