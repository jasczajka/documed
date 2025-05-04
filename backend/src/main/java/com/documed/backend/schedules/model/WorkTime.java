package com.documed.backend.schedules.model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class WorkTime {
  private int id;
  private final int userId;
  private final DayOfWeek dayOfWeek;
  private LocalTime startTime;
  private LocalTime endTime;
}
