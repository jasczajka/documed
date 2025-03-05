package com.documed.backend.schedules;

import com.documed.backend.users.User;
import lombok.AllArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@AllArgsConstructor
public class WorkTime {
    private final int id;
    private final User user;
    private final DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
}
