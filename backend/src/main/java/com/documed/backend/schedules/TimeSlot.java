package com.documed.backend.schedules;

import com.documed.backend.users.User;

import java.util.Date;

public class TimeSlot {
    private int id;
    private User doctor;
    private User patient;
    //TODO start end of slot
    private Date date;
    private boolean isBusy;
}
