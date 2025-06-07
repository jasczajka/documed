package com.documed.backend.schedules;

import com.documed.backend.auth.annotations.StaffOnly;
import com.documed.backend.schedules.dtos.AvailableTimeSlotDTO;
import com.documed.backend.schedules.dtos.FreeDaysDTO;
import com.documed.backend.schedules.dtos.TimeSlotMapper;
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

  //TODO annotation ward clerk only
  @PostMapping("/freeDay")
  @Operation(summary = "Create new FreeDay for doctor")
  public ResponseEntity<String> createFreeDay(@RequestBody @Valid FreeDaysDTO freeDaysDTO) {
    freeDaysService.createFreeDay(freeDaysDTO);
    return new ResponseEntity<>("FreeDay created", HttpStatus.CREATED);
  }
}
