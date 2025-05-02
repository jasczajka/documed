package com.documed.backend.schedules;

import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
public class WorkTimeDTO {
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
}
