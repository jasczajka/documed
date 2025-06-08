package com.documed.backend.users;

import com.documed.backend.auth.annotations.StaffOnly;
import com.documed.backend.auth.annotations.WardClerkOnly;
import com.documed.backend.auth.exceptions.UserNotFoundException;
import com.documed.backend.schedules.FreeDaysService;
import com.documed.backend.schedules.dtos.FreeDaysMapper;
import com.documed.backend.schedules.model.FreeDays;
import com.documed.backend.users.dtos.DoctorDetailsDTO;
import com.documed.backend.users.dtos.UpdateDoctorSpecializationsDTO;
import com.documed.backend.users.exceptions.UserNotDoctorException;
import com.documed.backend.users.model.Specialization;
import com.documed.backend.users.model.User;
import com.documed.backend.users.model.UserRole;
import com.documed.backend.users.services.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/doctors")
public class DoctorsController {

  private final UserService userService;
  private final FreeDaysService freeDaysService;

  // TODO pacjent lekarz
  // zmiana from Janek - potrzebujemy tego dla ward clerka tez
  @StaffOnly
  @GetMapping("/{id}")
  public ResponseEntity<DoctorDetailsDTO> getDoctorDetails(@PathVariable("id") int userId) {
    User user =
        userService.getById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
    if (user.getRole() != UserRole.DOCTOR) {
      throw new UserNotDoctorException("User is not a doctor");
    }
    DoctorDetailsDTO dto = mapUserToDoctorDetailsDTO(user);
    return new ResponseEntity<>(dto, HttpStatus.OK);
  }

  // TODO annotation clerk
  @WardClerkOnly
  @StaffOnly
  @PatchMapping("/{id}")
  public ResponseEntity<List<Integer>> updateDoctorSpecializations(
      @PathVariable("id") int userId, @Valid @RequestBody UpdateDoctorSpecializationsDTO request) {
    this.userService.updateUserSpecializations(userId, request.getSpecializationIds());
    return new ResponseEntity<>(request.getSpecializationIds(), HttpStatus.OK);
  }

  @GetMapping
  public ResponseEntity<List<DoctorDetailsDTO>> getAllDoctors() {
    List<User> allDoctors = this.userService.getAllByRole(UserRole.DOCTOR);
    List<DoctorDetailsDTO> dtos = allDoctors.stream().map(this::mapUserToDoctorDetailsDTO).toList();
    return new ResponseEntity<>(dtos, HttpStatus.OK);
  }

  private DoctorDetailsDTO mapUserToDoctorDetailsDTO(User user) {
    if (user.getRole() != UserRole.DOCTOR) {
      throw new UserNotDoctorException("User is not a doctor");
    }
    List<Specialization> specializations = userService.getUserSpecializationsById(user.getId());
    List<FreeDays> freeDaysList = freeDaysService.getFreeDaysByUserId(user.getId());
    return DoctorDetailsDTO.builder()
        .id(user.getId())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .email(user.getEmail())
        .specializations(specializations)
        .freeDays(freeDaysList.stream().map(FreeDaysMapper::toDTO).toList())
        .build();
  }
}
