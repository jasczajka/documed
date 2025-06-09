package com.documed.backend.visits;

import com.documed.backend.auth.AuthService;
import com.documed.backend.auth.annotations.DoctorOnly;
import com.documed.backend.auth.annotations.DoctorOrPatient;
import com.documed.backend.auth.annotations.StaffOnly;
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
import java.time.LocalDate;
import java.time.Period;
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

  private static final Period DEFAULT_VISIT_LOOKBACK_PERIOD = Period.ofMonths(3);

  private LocalDate resolveStartDate(LocalDate inputStartDate) {
    return (inputStartDate != null)
        ? inputStartDate
        : LocalDate.now().minus(DEFAULT_VISIT_LOOKBACK_PERIOD);
  }

  @StaffOnly
  @GetMapping
  @Operation(summary = "Get all visits")
  public ResponseEntity<List<VisitWithDetails>> getAllVisits(
      @RequestParam(required = false) LocalDate startDate) {

    LocalDate resolvedStart = resolveStartDate(startDate);

    List<VisitWithDetails> visits = visitService.getAllWithDetailsBetweenDates(resolvedStart);
    return new ResponseEntity<>(visits, HttpStatus.OK);
  }

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

  @GetMapping("/patient")
  @Operation(summary = "Get all visits for logged in patient")
  public ResponseEntity<List<VisitWithDetails>> getVisitsForCurrentPatient(
      @RequestParam(required = false) LocalDate startDate) {

    LocalDate resolvedStart = resolveStartDate(startDate);

    List<VisitWithDetails> visits =
        visitService.getVisitsForCurrentPatientWithDetailsBetweenDates(resolvedStart);
    return new ResponseEntity<>(visits, HttpStatus.OK);
  }

  @Secured({"WARD_CLERK", "DOCTOR", "NURSE", "PATIENT"})
  @GetMapping("/patient/{id}")
  @Operation(summary = "Get all visits for selected patient")
  public ResponseEntity<List<VisitWithDetails>> getVisitsByPatientId(
      @PathVariable("id") int patientId, @RequestParam(required = false) LocalDate startDate) {

    int currentUserId = authService.getCurrentUserId();
    UserRole userRole = authService.getCurrentUserRole();

    if (userRole == UserRole.PATIENT && currentUserId != patientId) {
      throw new UnauthorizedException("You are not authorized to view these visits");
    }

    LocalDate resolvedStart = resolveStartDate(startDate);

    List<VisitWithDetails> visits =
        visitService.getVisitsByPatientIdWithDetailsBetweenDates(patientId, resolvedStart);
    return new ResponseEntity<>(visits, HttpStatus.OK);
  }

  @Secured({"WARD_CLERK", "DOCTOR"})
  @Operation(summary = "get all visits assigned for selected doctor")
  @GetMapping("/doctor/{id}")
  public ResponseEntity<List<VisitWithDetails>> getVisitsByDoctorId(
      @PathVariable("id") int doctorId, @RequestParam(required = false) LocalDate startDate) {

    LocalDate resolvedStart = resolveStartDate(startDate);

    List<VisitWithDetails> visits =
        visitService.getVisitsByDoctorIdWithDetailsBetweenDates(doctorId, resolvedStart);
    return new ResponseEntity<>(visits, HttpStatus.OK);
  }

  @DoctorOnly
  @Operation(summary = "get all visits assigned for logged in doctor")
  @GetMapping("/doctor")
  public ResponseEntity<List<VisitWithDetails>> getVisitsForCurrentDoctor(
      @RequestParam(required = false) LocalDate startDate) {

    LocalDate resolvedStart = resolveStartDate(startDate);

    List<VisitWithDetails> visits =
        visitService.getVisitsForCurrentDoctorWithDetailsBetweenDates(resolvedStart);
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
