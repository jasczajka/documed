package com.documed.backend.visits;

import com.documed.backend.auth.AuthService;
import com.documed.backend.auth.annotations.DoctorOnly;
import com.documed.backend.auth.annotations.DoctorOrPatient;
import com.documed.backend.auth.annotations.StaffOnly;
import com.documed.backend.auth.annotations.WardClerkOnly;
import com.documed.backend.auth.exceptions.UnauthorizedException;
import com.documed.backend.users.model.UserRole;
import com.documed.backend.visits.dtos.GiveFeedbackDTO;
import com.documed.backend.visits.dtos.ScheduleVisitDTO;
import com.documed.backend.visits.dtos.UpdateVisitDTO;
import com.documed.backend.visits.exceptions.CancelVisitException;
import com.documed.backend.visits.model.Feedback;
import com.documed.backend.visits.model.Visit;
import com.documed.backend.visits.model.VisitWithDetails;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/visits")
public class VisitController {

  private final VisitService visitService;
  private final AuthService authService;

  // TODO clerk i lekarz
  // filtr na date na ostatni miesiac i na bazie uzywac
  @StaffOnly
  @GetMapping
  @Operation(summary = "Get all visits")
  public ResponseEntity<List<VisitWithDetails>> getAllVisits() {

    return new ResponseEntity<>(visitService.getAllWithDetails(), HttpStatus.OK);
  }

  // TODO lekarz i pacjent
  @DoctorOrPatient
  @GetMapping("/{id}")
  @Operation(summary = "Get visit by id")
  public ResponseEntity<VisitWithDetails> getVisitById(@PathVariable("id") int id) {
    VisitWithDetails visit = visitService.getByIdWithDetails(id);

    UserRole userRole = authService.getCurrentUserRole();
    int patientId = visit.getPatientId();
    int currentUserId = authService.getCurrentUserId();

    if (userRole == UserRole.PATIENT && currentUserId != patientId) {
      throw new UnauthorizedException("You are not authorized to view this visit");
    }

    return new ResponseEntity<>(visit, HttpStatus.OK);
  }

  // TODO pacjent i clerk
  @PreAuthorize("hasAnyRole('PATIENT', 'WARD_CLERK')")
  @PostMapping
  @Operation(summary = "schedule/create visit")
  public ResponseEntity<VisitWithDetails> scheduleVisit(
      @RequestBody ScheduleVisitDTO scheduleVisitDTO) {
    Visit visit = visitService.scheduleVisit(scheduleVisitDTO);

    return new ResponseEntity<>(visitService.getByIdWithDetails(visit.getId()), HttpStatus.CREATED);
  }

  @DoctorOnly
  @PatchMapping("/{id}/start")
  @Operation(summary = "start visit")
  public ResponseEntity<Void> startVisit(@PathVariable("id") int id) {
    visitService.startVisit(id);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  // TODO annotation patient and clerk
  @PreAuthorize("hasAnyRole('PATIENT', 'WARD_CLERK')")
  @PatchMapping("/{id}/cancel")
  @Operation(summary = "cancel visit")
  public ResponseEntity<Void> cancelPlannedVisit(@PathVariable("id") int id) {

    int currentUserId = authService.getCurrentUserId();
    UserRole userRole = authService.getCurrentUserRole();
    int patientId = visitService.getByIdWithDetails(id).getPatientId();

    if (userRole == UserRole.PATIENT && currentUserId != patientId) {
      throw new UnauthorizedException("You are not authorized to cancel this visit");
    }

    if (visitService.cancelVisit(id)) {
      return new ResponseEntity<>(HttpStatus.OK);
    } else {
      throw new CancelVisitException("Failed to cancel visit " + id);
    }
  }

  @Operation(summary = "get all visits for logged in patient")
  @GetMapping("/patient")
  public ResponseEntity<List<VisitWithDetails>> getVisitsForCurrentPatient() {
    List<VisitWithDetails> visits = visitService.getVisitsForCurrentPatientWithDetails();

    return new ResponseEntity<>(visits, HttpStatus.OK);
  }

  // TODO annotation pacjent lekarz i rejestrator
  @PreAuthorize("hasAnyRole('WARD_CLERK', 'DOCTOR', 'PATIENT')")
  @Operation(summary = "get all visits for selected patient")
  @GetMapping("/patient/{id}")
  public ResponseEntity<List<VisitWithDetails>> getVisitsByPatientId(
      @PathVariable("id") int patientId) {

    int currentUserId = authService.getCurrentUserId();
    UserRole userRole = authService.getCurrentUserRole();

    if (userRole == UserRole.PATIENT && currentUserId != patientId) {
      throw new UnauthorizedException("You are not authorized to view these visits");
    }

    List<VisitWithDetails> visits = visitService.getVisitsByPatientIdWithDetails(patientId);

    return new ResponseEntity<>(visits, HttpStatus.OK);
  }

  // TODO annoatation clerk
  @WardClerkOnly
  @Operation(summary = "get all visits assigned for selected doctor")
  @GetMapping("/doctor/{id}")
  public ResponseEntity<List<VisitWithDetails>> getVisitsByDoctorId(
      @PathVariable("id") int doctorId) {
    List<VisitWithDetails> visits = visitService.getVisitsByDoctorIdWithDetails(doctorId);

    return new ResponseEntity<>(visits, HttpStatus.OK);
  }

  @DoctorOnly
  @Operation(summary = "get all visits assigned for logged in doctor")
  @GetMapping("/doctor")
  public ResponseEntity<List<VisitWithDetails>> getVisitsForCurrentDoctor() {
    List<VisitWithDetails> visits = visitService.getVisitsForCurrentDoctorWithDetails();

    return new ResponseEntity<>(visits, HttpStatus.OK);
  }

  @DoctorOnly
  @PatchMapping("/{id}")
  @Operation(summary = "update visit data")
  public ResponseEntity<VisitWithDetails> updateVisit(
      @PathVariable("id") int visitId, @RequestBody UpdateVisitDTO updateVisitDTO) {
    Visit updatedVisit = visitService.updateVisit(visitId, updateVisitDTO);
    return new ResponseEntity<>(
        visitService.getByIdWithDetails(updatedVisit.getId()), HttpStatus.OK);
  }

  @DoctorOnly
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

  @GetMapping("/calculate-cost")
  @Operation(summary = "Calculate visit cost")
  public ResponseEntity<BigDecimal> calculateVisitCost(
      @RequestParam int patientId, @RequestParam int serviceId) {
    return new ResponseEntity<>(
        visitService.calculateTotalCost(serviceId, patientId), HttpStatus.OK);
  }

  @Secured("PATIENT")
  @PostMapping("/{id}/feedback")
  public ResponseEntity<Void> giveFeedbackForVisit(
      @PathVariable("id") int visitId, @RequestBody @Valid GiveFeedbackDTO dto) {
    Feedback feedback =
        Feedback.builder().rating(dto.getRating()).text(dto.getMessage()).visitId(visitId).build();

    visitService.giveFeedback(feedback);
    return ResponseEntity.ok().build();
  }
}
