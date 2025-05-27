package com.documed.backend.users;

import com.documed.backend.auth.annotations.StaffOnly;
import com.documed.backend.auth.annotations.StaffOnlyOrSelf;
import com.documed.backend.auth.exceptions.UserNotFoundException;
import com.documed.backend.users.dtos.PatientDetailsDTO;
import com.documed.backend.users.exceptions.UserNotPatientException;
import com.documed.backend.users.model.User;
import com.documed.backend.users.model.UserRole;
import com.documed.backend.users.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/patients")
public class PatientsController {

  private final UserService userService;

  @StaffOnlyOrSelf
  @GetMapping("/{id}")
  public ResponseEntity<PatientDetailsDTO> getPatientDetails(@PathVariable("id") int userId) {
    User user =
        userService.getById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
    if (user.getRole() != UserRole.PATIENT) {
      throw new UserNotPatientException("User is not a patient");
    }

    PatientDetailsDTO value =
        PatientDetailsDTO.builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .birthdate(user.getBirthDate())
            .build();

    return new ResponseEntity<>(value, HttpStatus.OK);
  }

  @Operation(summary = "Set patient subscription")
  @PatchMapping("/{id}/subscription")
  public ResponseEntity<String> updateUserSubscription(
      @PathVariable("id") int userId, @RequestBody int subscriptionId) {
    userService.updateUserSubscription(userId, subscriptionId);
    return new ResponseEntity<>("Subscription updated", HttpStatus.OK);
  }

  @Operation(summary = "Cancel patient subscription")
  @PatchMapping("/{id}/subscription/cancel")
  public ResponseEntity<String> updateUserSubscription(@PathVariable("id") int userId) {
    userService.updateUserSubscription(userId, 0);
    return new ResponseEntity<>("Subscription cancelled", HttpStatus.OK);
  }

  @StaffOnly
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePatientPersonalData(@PathVariable("id") int userId) {
    this.userService.deactivateUser(userId);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
