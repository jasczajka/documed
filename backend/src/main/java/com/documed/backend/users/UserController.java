package com.documed.backend.users;

import com.documed.backend.auth.AuthService;
import com.documed.backend.auth.annotations.StaffOnly;
import com.documed.backend.auth.annotations.StaffOnlyOrSelf;
import com.documed.backend.auth.exceptions.UserNotFoundException;
import com.documed.backend.users.dtos.DoctorDetailsDTO;
import com.documed.backend.users.dtos.UpdateDoctorSpecializationsDTO;
import com.documed.backend.users.exceptions.UserNotDoctorException;
import com.documed.backend.users.model.Specialization;
import com.documed.backend.users.model.User;
import com.documed.backend.users.model.UserRole;
import com.documed.backend.users.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
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

  @StaffOnly
  @PatchMapping("/{id}")
  public ResponseEntity<List<Integer>> updateDoctorSpecializations(
      @PathVariable("id") int userId, @Valid @RequestBody UpdateDoctorSpecializationsDTO request) {
    this.userService.updateUserSpecializations(userId, request.getSpecializationIds());
    return new ResponseEntity<>(request.getSpecializationIds(), HttpStatus.OK);
  }

  @StaffOnlyOrSelf
  @GetMapping("/{id}/basic_info")
  public ResponseEntity<DoctorDetailsDTO> getDoctorDetails(@PathVariable("id") int userId) {
    Optional<User> optionalUser = userService.getById(userId);
    if (optionalUser.isEmpty()) {
      throw new UserNotFoundException("User not found");
    }
    User user = optionalUser.get();
    if (user.getRole() != UserRole.DOCTOR) {
      System.out.println(user);
      throw new UserNotDoctorException("User is not a doctor");
    }
    List<Specialization> doctorSpecializations =
        userService.getUserSpecializationsById(user.getId());
    DoctorDetailsDTO value =
        DoctorDetailsDTO.builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .specializations(doctorSpecializations)
            .build();

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
          description = "To cancel subscription set subscriptionId to null or 0"
  )
  @PatchMapping("/{id}/subscription")
  public ResponseEntity<String> updateUserSubscription(
          @PathVariable("id") int userId, @RequestBody int subscriptionId) {
    userService.updateUserSubscription(userId, subscriptionId);
    return new ResponseEntity<>("Subscription updated", HttpStatus.OK);
  }

}
