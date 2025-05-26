package com.documed.backend.schedules;

import com.documed.backend.auth.annotations.StaffOnly;
import com.documed.backend.schedules.dtos.AvailableTimeSlotDTO;
import com.documed.backend.schedules.dtos.TimeSlotMapper;
import com.documed.backend.schedules.model.TimeSlot;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@Controller
@RequestMapping("/timeslots")
public class TimeSlotController {

  private final TimeSlotService timeSlotService;

  @StaffOnly
  @GetMapping("/{id}")
  @Operation(summary = "Get timeslot by id")
  public ResponseEntity<TimeSlot> getTimeSlotById(@PathVariable("id") int id) {
    return timeSlotService
        .getTimeSlotById(id)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/doctors/{id}/available-timeslots")
  @Operation(summary = "Get available timeslots for doctor by id and required visit length")
  public ResponseEntity<List<AvailableTimeSlotDTO>> getAvailableFirstTimeSlotsByDoctor(
      @PathVariable("id") int id, @RequestParam("neededTimeSlots") int neededTimeSlots) {

    List<TimeSlot> timeSlots =
        timeSlotService.getAvailableFirstTimeSlotsByDoctor(id, neededTimeSlots);

    List<AvailableTimeSlotDTO> dtos =
        timeSlots.stream().map(TimeSlotMapper::toDto).toList();

    return ResponseEntity.ok(dtos);
  }
}
