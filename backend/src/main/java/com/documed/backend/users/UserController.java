package com.documed.backend.users;

import com.documed.backend.auth.AuthService;
import com.documed.backend.auth.annotations.SelfDataOnly;
import com.documed.backend.users.model.Specialization;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/user")
public class UserController {
  private final UserService userService;
  private final AuthService authService;

  @SelfDataOnly
  @PatchMapping()
  public ResponseEntity<List<Specialization>> toggleEmailNotifications() {
    int userId = authService.getCurrentUserId();
    this.userService.toggleEmailNotificationsById(userId);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
