package com.documed.backend.schedules;

import com.documed.backend.users.User;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class WorkTime {
    private int id;
    private User doctor;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
}
