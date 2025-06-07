package com.documed.backend.schedules;

import com.documed.backend.auth.annotations.StaffOnly;
import com.documed.backend.schedules.dtos.*;
import com.documed.backend.schedules.model.FreeDays;
import com.documed.backend.schedules.model.TimeSlot;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@Controller
@RequestMapping("/timeslots")
public class TimeSlotController {

  private final TimeSlotService timeSlotService;
  private final FreeDaysService freeDaysService;

  @StaffOnly
  @GetMapping("/{id}")
  @Operation(summary = "Get timeslot by id")
  public ResponseEntity<TimeSlot> getTimeSlotById(@PathVariable("id") int id) {
    return timeSlotService
        .getTimeSlotById(id)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/doctors/{doctor_id}/available-timeslots")
  @Operation(summary = "Get available timeslots for doctor by id and required visit length")
  public ResponseEntity<List<AvailableTimeSlotDTO>> getAvailableFirstTimeSlotsByDoctorAndFacility(
      @PathVariable("doctor_id") int doctorId,
      @RequestParam("neededTimeSlots") int neededTimeSlots,
      @RequestParam("facilityId") int facilityId) {

    List<TimeSlot> timeSlots =
        timeSlotService.getAvailableFirstTimeSlotsByDoctorAndFacility(
            doctorId, neededTimeSlots, facilityId);

    List<AvailableTimeSlotDTO> dtos = timeSlots.stream().map(TimeSlotMapper::toDto).toList();

    return ResponseEntity.ok(dtos);
  }

  @PostMapping("/freeDay")
  @Operation(summary = "Create new FreeDays for doctor")
  public ResponseEntity<FreeDaysReturnDTO> createFreeDays(
      @RequestBody @Valid FreeDaysDTO freeDaysDTO) {
    FreeDays created = freeDaysService.createFreeDays(freeDaysDTO);
    return new ResponseEntity<>(FreeDaysMapper.toDTO(created), HttpStatus.CREATED);
  }

  @DeleteMapping("/freeDay/{id}")
  @Operation(summary = "Cancel existing FreeDays for doctor")
  public ResponseEntity<Void> cancelFreeDays(@PathVariable("id") int id) {
    freeDaysService.cancelFreeDays(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
