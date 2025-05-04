package com.documed.backend.schedules;

import com.documed.backend.auth.annotations.StaffOnly;
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

  @StaffOnly
  @PostMapping("/{user_id}")
  @Operation(summary = "Create worktime for user")
  public ResponseEntity<WorkTime> createWorkTime(
      @PathVariable("user_id") int userId, @Valid @RequestBody WorkTimeDTO dto) {
    WorkTime createdWorkTime =
        workTimeService.createWorkTime(
            WorkTime.builder()
                .userId(userId)
                .dayOfWeek(dto.getDayOfWeek())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .build());
    return ResponseEntity.ok(createdWorkTime);
  }

  @StaffOnly
  @GetMapping("/{user_id}")
  @Operation(summary = "Get all worktimes for user")
  public ResponseEntity<List<WorkTime>> getWorkTimesForUser(@PathVariable("user_id") int userId) {
    List<WorkTime> workTimes = workTimeService.getWorkTimesForUser(userId);
    return ResponseEntity.ok(workTimes);
  }

  @StaffOnly
  @PutMapping("/{user_id}")
  @Operation(summary = "Update worktimes for user")
  public ResponseEntity<List<WorkTime>> updateWorkTimesForUser(
      @PathVariable("user_id") int userId, @RequestBody List<@Valid WorkTimeDTO> workTimes) {
    List<WorkTime> updatedWorkTimes = workTimeService.updateWorkTimes(workTimes, userId);
    return ResponseEntity.ok(updatedWorkTimes);
  }
}
