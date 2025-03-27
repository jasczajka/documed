package com.documed.backend.schedules;

import com.documed.backend.users.User;
import java.time.LocalTime;
import java.util.Date;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TimeSlot {
  private int id;
  private final User doctor;
  private final LocalTime startTime;
  private final LocalTime endTime;
  private final Date date;
  private boolean isBusy;
}
