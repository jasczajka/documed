package com.documed.backend.schedules;

import java.time.DayOfWeek;
import java.time.LocalTime;
import lombok.Data;

@Data
public class WorkTimeDTO {
  private DayOfWeek dayOfWeek;
  private LocalTime startTime;
  private LocalTime endTime;
}
