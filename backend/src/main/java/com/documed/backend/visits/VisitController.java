package com.documed.backend.visits;

import com.documed.backend.auth.annotations.StaffOnly;
import com.documed.backend.exceptions.BadRequestException;
import com.documed.backend.exceptions.NotFoundException;
import com.documed.backend.schedules.TimeSlotService;
import com.documed.backend.schedules.model.TimeSlot;
import com.documed.backend.services.ServiceService;
import com.documed.backend.services.model.Service;
import com.documed.backend.users.UserService;
import com.documed.backend.users.model.User;
import com.documed.backend.visits.dtos.UpdateVisitDTO;
import com.documed.backend.visits.dtos.VisitDTO;
import com.documed.backend.visits.dtos.VisitMapper;
import com.documed.backend.visits.exceptions.CancelVisitException;
import com.documed.backend.visits.model.ScheduleVisitDTO;
import com.documed.backend.visits.model.Visit;
import com.documed.backend.visits.model.VisitStatus;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/visits")
public class VisitController {

  private final VisitService visitService;
  private final UserService userService;
  private final TimeSlotService timeSlotService;
  private final ServiceService serviceService;

  private static final Logger log = LoggerFactory.getLogger(VisitController.class);

  @GetMapping("/{id}")
  @Operation(summary = "Get visit by id")
  public ResponseEntity<VisitDTO> getVisitById(@PathVariable("id") int id) {
    Visit visit = visitService.getById(id);
    return new ResponseEntity<>(this.enrichVisitToDto(visit), HttpStatus.OK);
  }

  @PostMapping
  @Operation(summary = "schedule/create visit")
  public ResponseEntity<VisitDTO> scheduleVisit(@RequestBody ScheduleVisitDTO scheduleVisitDTO) {
    Visit visit = visitService.scheduleVisit(scheduleVisitDTO);

    return new ResponseEntity<>(this.enrichVisitToDto(visit), HttpStatus.CREATED);
  }

  @StaffOnly
  @PatchMapping("/{id}/start")
  @Operation(summary = "start visit")
  public ResponseEntity<Void> startVisit(@PathVariable("id") int id) {
    visitService.startVisit(id);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @StaffOnly
  @PatchMapping("/{id}/cancel")
  @Operation(summary = "cancel visit")
  public ResponseEntity<Void> cancelPlannedVisit(@PathVariable("id") int id) {

    if (visitService.cancelVisit(id)) {
      return new ResponseEntity<>(HttpStatus.OK);
    } else {
      throw new CancelVisitException("Failed to cancel visit " + id);
    }
  }

  @Operation(summary = "get all visits for logged in patient")
  @GetMapping("/patient")
  public ResponseEntity<List<VisitDTO>> getVisitsForCurrentPatient() {
    List<Visit> visits = visitService.getVisitsForCurrentPatient();

    return new ResponseEntity<>(
        visits.stream().map(this::enrichVisitToDto).toList(), HttpStatus.OK);
  }

  @StaffOnly
  @Operation(summary = "get all visits for selected patient")
  @GetMapping("/patient/{id}")
  public ResponseEntity<List<VisitDTO>> getVisitsByPatientId(@PathVariable("id") int patientId) {
    List<Visit> visits = visitService.getVisitsByPatientId(patientId);

    return new ResponseEntity<>(
        visits.stream().map(this::enrichVisitToDto).toList(), HttpStatus.OK);
  }

  @StaffOnly
  @Operation(summary = "get all visits assigned for selected doctor")
  @GetMapping("/doctor/{id}")
  public ResponseEntity<List<VisitDTO>> getVisitsByDoctorId(@PathVariable("id") int doctorId) {
    List<Visit> visits = visitService.getVisitsByDoctorId(doctorId);

    return new ResponseEntity<>(
        visits.stream().map(this::enrichVisitToDto).toList(), HttpStatus.OK);
  }

  @Operation(summary = "get all visits assigned for logged in doctor")
  @GetMapping("/doctor")
  public ResponseEntity<List<VisitDTO>> getVisitsForCurrentDoctor() {
    List<Visit> visits = visitService.getVisitsForCurrentDoctor();

    return new ResponseEntity<>(
        visits.stream().map(this::enrichVisitToDto).toList(), HttpStatus.OK);
  }

  @StaffOnly
  @PatchMapping("/{id}")
  @Operation(summary = "update visit data")
  public ResponseEntity<VisitDTO> updateVisit(
      @PathVariable("id") int visitId, @RequestBody UpdateVisitDTO updateVisitDTO) {
    Visit updatedVisit = visitService.updateVisit(visitId, updateVisitDTO);
    return new ResponseEntity<>(enrichVisitToDto(updatedVisit), HttpStatus.OK);
  }

  @StaffOnly
  @PatchMapping("/{id}/close")
  @Operation(summary = "finish visit")
  public ResponseEntity<Void> finishVisit(
      @PathVariable("id") int visitId, @RequestBody UpdateVisitDTO updateVisitDTO) {
    if (visitService.closeVisit(visitId, updateVisitDTO)) {
      return new ResponseEntity<>(HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  private VisitDTO enrichVisitToDto(Visit visit) {
    User patient =
        userService
            .getById(visit.getPatientId())
            .orElseThrow(
                () -> {
                  log.warn("Patient not found with ID: {}", visit.getPatientId());
                  return new NotFoundException("Patient not found");
                });
    List<TimeSlot> visitTimeSlots = this.timeSlotService.getTimeSlotsForVisit(visit.getId());
    if (visit.getStatus() != VisitStatus.CANCELLED && visitTimeSlots.isEmpty()) {
      log.warn("No timeslots found for not cancelled visit ID: {}", visit.getId());
      throw new BadRequestException(
          "This visit is not cancelled and doesn't have any timeslots assigned");
    }
    User doctor =
        userService
            .getById(visit.getDoctorId())
            .orElseThrow(
                () -> {
                  log.warn("Doctor not found with ID: {}", visit.getDoctorId());
                  return new NotFoundException("Doctor not found");
                });

    Service service =
        serviceService
            .getById(visit.getServiceId())
            .orElseThrow(
                () -> {
                  log.warn("Service not found with ID: {}", visit.getServiceId());
                  return new NotFoundException("Service not found");
                });

    return VisitMapper.toDto(visit, patient, doctor, service, visitTimeSlots);
  }
}
