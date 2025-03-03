package com.documed.backend.schedules;

import com.documed.backend.users.User;

import java.time.LocalTime;
import java.util.Date;

public class TimeSlot {
    private int id;
    private User doctor;
    private User patient;
    private LocalTime startTime;
    private LocalTime endTime;
    private Date date;
    private boolean isBusy;
}
