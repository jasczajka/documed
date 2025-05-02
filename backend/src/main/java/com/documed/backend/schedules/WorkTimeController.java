package com.documed.backend.schedules;

import com.documed.backend.schedules.model.WorkTime;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/worktime")
public class WorkTimeController {

    private final WorkTimeService workTimeService;

    @PostMapping("/{user_id}")
    public ResponseEntity<WorkTime> createWorkTime(
            @PathVariable("user_id") int userId,
            @RequestBody WorkTimeDTO dto) {
        WorkTime createdWorkTime = workTimeService
                .createWorkTime(
                    WorkTime.builder()
                            .userId(userId)
                            .dayOfWeek(dto.getDayOfWeek())
                            .startTime(dto.getStartTime())
                            .endTime(dto.getEndTime())
                            .build()
                );
        return ResponseEntity.ok(createdWorkTime);
    }

}
