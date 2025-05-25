package com.documed.backend.visits;

import com.documed.backend.auth.annotations.StaffOnly;
import com.documed.backend.visits.dtos.UpdateVisitDTO;
import com.documed.backend.visits.dtos.VisitDTO;
import com.documed.backend.visits.dtos.VisitMapper;
import com.documed.backend.visits.exceptions.CancelVisitException;
import com.documed.backend.visits.model.ScheduleVisitDTO;
import com.documed.backend.visits.model.Visit;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
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
  public ResponseEntity<VisitDTO> getVisitById(@PathVariable("id") int id) {
    Visit visit = visitService.getById(id);
    return new ResponseEntity<>(VisitMapper.toDto(visit), HttpStatus.OK);
  }

  @PostMapping
  @Operation(summary = "schedule/create visit")
  public ResponseEntity<VisitDTO> scheduleVisit(@RequestBody ScheduleVisitDTO scheduleVisitDTO) {
    Visit visit = visitService.scheduleVisit(scheduleVisitDTO);

    return new ResponseEntity<>(VisitMapper.toDto(visit), HttpStatus.CREATED);
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

    return new ResponseEntity<>(visits.stream().map(VisitMapper::toDto).toList(), HttpStatus.OK);
  }

  @StaffOnly
  @Operation(summary = "get all visits for selected patient")
  @GetMapping("/patient/{id}")
  public ResponseEntity<List<VisitDTO>> getVisitsByPatientId(@PathVariable("id") int patientId) {
    List<Visit> visits = visitService.getVisitsByPatientId(patientId);

    return new ResponseEntity<>(visits.stream().map(VisitMapper::toDto).toList(), HttpStatus.OK);
  }

  @StaffOnly
  @Operation(summary = "get all visits assigned for selected doctor")
  @GetMapping("/doctor/{id}")
  public ResponseEntity<List<VisitDTO>> getVisitsByDoctorId(@PathVariable("id") int doctorId) {
    List<Visit> visits = visitService.getVisitsByDoctorId(doctorId);

    return new ResponseEntity<>(visits.stream().map(VisitMapper::toDto).toList(), HttpStatus.OK);
  }

  @Operation(summary = "get all visits assigned for logged in doctor")
  @GetMapping("/doctor")
  public ResponseEntity<List<VisitDTO>> getVisitsForCurrentDoctor() {
    List<Visit> visits = visitService.getVisitsForCurrentDoctor();

    return new ResponseEntity<>(visits.stream().map(VisitMapper::toDto).toList(), HttpStatus.OK);
  }

  @StaffOnly
  @PatchMapping("/{id}")
  @Operation(summary = "update visit data")
  public ResponseEntity<VisitDTO> updateVisit(
      @PathVariable("id") int visitId, @RequestBody UpdateVisitDTO updateVisitDTO) {
    Visit updatedVisit = visitService.updateVisit(visitId, updateVisitDTO);
    return new ResponseEntity<>(VisitMapper.toDto(updatedVisit), HttpStatus.OK);
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
}
