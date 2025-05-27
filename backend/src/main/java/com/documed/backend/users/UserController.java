package com.documed.backend.users;

import com.documed.backend.auth.AuthService;
import com.documed.backend.users.model.Specialization;
import com.documed.backend.users.services.UserService;
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
}
