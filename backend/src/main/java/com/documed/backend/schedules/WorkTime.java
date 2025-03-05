package com.documed.backend.schedules;

import com.documed.backend.users.User;
import java.time.DayOfWeek;
import java.time.LocalTime;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WorkTime {
  private final int id;
  private final User user;
  private final DayOfWeek dayOfWeek;
  private LocalTime startTime;
  private LocalTime endTime;
}
