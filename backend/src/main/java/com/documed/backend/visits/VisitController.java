package com.documed.backend.visits;

import com.documed.backend.auth.annotations.SelfDataOnly;
import com.documed.backend.auth.annotations.StaffOnly;
import com.documed.backend.visits.model.ScheduleVisitDTO;
import com.documed.backend.visits.model.Visit;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/visits")
public class VisitController {

  private final VisitService visitService;

  @GetMapping("/{id}")
  @Operation(summary = "Get visit by id")
  public ResponseEntity<Optional<Visit>> getVisitById(@PathVariable("id") int id) {
    Optional<Visit> visit = visitService.getById(id);
    if (visit.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      return new ResponseEntity<>(visit, HttpStatus.OK);
    }
  }

  @PostMapping
  @Operation(summary = "schedule/create visit")
  public ResponseEntity<Visit> scheduleVisit(@RequestBody ScheduleVisitDTO scheduleVisitDTO) {
    Visit visit = visitService.scheduleVisit(scheduleVisitDTO);

    return new ResponseEntity<>(visit, HttpStatus.CREATED);
  }

  @StaffOnly
  @PatchMapping("/{id}/start")
  @Operation(summary = "start visit")
  public ResponseEntity<Boolean> startVisit(@PathVariable("id") int id) {
    visitService.startVisit(id);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @StaffOnly
  @PatchMapping("/{id}/cancel")
  @Operation(summary = "cancel visit")
  public ResponseEntity<Boolean> cancelPlannedVisit(@PathVariable("id") int id) {
    // visitService.cancelVisit(id);
    // TODO: implement cancel visit
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @SelfDataOnly
  @Operation(summary = "get all visits for logged in patient")
  @GetMapping("/patient")
  public ResponseEntity<List<Visit>> getVisitsForCurrentPatient() {
    List<Visit> visits = visitService.getVisitsForCurrentPatient();

    return new ResponseEntity<>(visits, HttpStatus.OK);
  }

  @StaffOnly
  @Operation(summary = "get all visits for selected patient")
  @GetMapping("/patient/{id}")
  public ResponseEntity<List<Visit>> getVisitsByPatientId(@PathVariable("id") int patientId) {
    List<Visit> visits = visitService.getVisitsByPatientId(patientId);

    return new ResponseEntity<>(visits, HttpStatus.OK);
  }

  @StaffOnly
  @Operation(summary = "get all visits assigned for selected doctor")
  @GetMapping("/doctor/{id}")
  public ResponseEntity<List<Visit>> getVisitsByDoctorId(@PathVariable("id") int doctorId) {
    List<Visit> visits = visitService.getVisitsByDoctorId(doctorId);

    return new ResponseEntity<>(visits, HttpStatus.OK);
  }

  @SelfDataOnly
  @Operation(summary = "get all visits assigned for logged in doctor")
  @GetMapping("/doctor")
  public ResponseEntity<List<Visit>> getVisitsForCurrentDoctor() {
    List<Visit> visits = visitService.getVisitsForCurrentDoctor();

    return new ResponseEntity<>(visits, HttpStatus.OK);
  }
}
