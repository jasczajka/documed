package com.documed.backend.users;

import com.documed.backend.auth.AuthService;
import com.documed.backend.auth.annotations.StaffOnly;
import com.documed.backend.users.model.Specialization;
import com.documed.backend.users.model.User;
import com.documed.backend.users.model.UserRole;
import com.documed.backend.users.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/user")
public class UserController {
  private final UserService userService;
  private final AuthService authService;

  @PatchMapping("/notifications")
  public ResponseEntity<List<Specialization>> toggleEmailNotifications() {
    this.userService.toggleEmailNotificationsById(authService.getCurrentUserId());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("/notifications")
  public ResponseEntity<Boolean> areNotificationsOn() {
    boolean value = this.userService.areNotificationsOn(authService.getCurrentUserId());
    return new ResponseEntity<>(value, HttpStatus.OK);
  }

  @GetMapping("/{id}/specializations")
  public ResponseEntity<List<Specialization>> getUserSpecializations(
      @PathVariable("id") int userId) {
    List<Specialization> specs = this.userService.getUserSpecializationsById(userId);
    return new ResponseEntity<>(specs, HttpStatus.OK);
  }

  @Operation(
      summary = "Set patient subscription",
      description = "To cancel subscription set subscriptionId to null or 0")
  @PatchMapping("/{id}/subscription")
  public ResponseEntity<String> updateUserSubscription(
      @PathVariable("id") int userId, @RequestBody int subscriptionId) {
    userService.updateUserSubscription(userId, subscriptionId);
    return new ResponseEntity<>("Subscription updated", HttpStatus.OK);
  }

  @StaffOnly
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePatientPersonalData(@PathVariable("id") int userId) {
    this.userService.deactivateUser(userId);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
