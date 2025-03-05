package com.documed.backend.schedules;

import com.documed.backend.users.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;
import java.util.Date;

@AllArgsConstructor
public class TimeSlot {
    private final int id;
    private final User doctor;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final Date date;
    private boolean isBusy;
}
