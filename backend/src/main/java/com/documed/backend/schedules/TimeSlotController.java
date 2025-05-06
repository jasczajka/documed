package com.documed.backend.schedules;

import com.documed.backend.auth.annotations.StaffOnly;
import com.documed.backend.schedules.model.TimeSlot;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
