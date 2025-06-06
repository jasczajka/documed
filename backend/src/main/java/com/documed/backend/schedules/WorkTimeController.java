package com.documed.backend.schedules;

import com.documed.backend.auth.annotations.StaffOnly;
import com.documed.backend.schedules.dtos.*;
import com.documed.backend.schedules.model.WorkTime;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@Validated
@RestController
@RequestMapping("/api/worktime")
public class WorkTimeController {

  private final WorkTimeService workTimeService;

  //TODO annotation
  @StaffOnly
  @GetMapping("/{user_id}")
  @Operation(summary = "Get all worktimes for user")
  public ResponseEntity<List<WorkTimeReturnDTO>> getWorkTimesForUser(
      @PathVariable("user_id") int userId) {
    List<WorkTime> workTimes = workTimeService.getWorkTimesForUser(userId);
    return ResponseEntity.ok(workTimes.stream().map(WorkTimeMapper::toDto).toList());
  }

  //TODO annotation ward clerk only
  @StaffOnly
  @PutMapping("/{user_id}")
  @Operation(summary = "Update worktimes for user")
  public ResponseEntity<List<WorkTimeReturnDTO>> updateWorkTimesForUser(
      @PathVariable("user_id") int userId, @RequestBody List<@Valid UploadWorkTimeDTO> workTimes) {
    List<WorkTime> updatedWorkTimes = workTimeService.updateWorkTimes(workTimes, userId);
    return ResponseEntity.ok(updatedWorkTimes.stream().map(WorkTimeMapper::toDto).toList());
  }
}
