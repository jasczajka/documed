package com.documed.backend.users;

import com.documed.backend.auth.annotations.StaffOnlyOrSelf;
import com.documed.backend.auth.exceptions.UserNotFoundException;
import com.documed.backend.users.dtos.PatientDetailsDTO;
import com.documed.backend.users.exceptions.UserNotPatientException;
import com.documed.backend.users.model.User;
import com.documed.backend.users.model.UserRole;
import com.documed.backend.users.services.UserService;
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
}
