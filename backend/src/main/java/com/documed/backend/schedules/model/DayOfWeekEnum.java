package com.documed.backend.schedules.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.DayOfWeek;

@Schema(enumAsRef = true)
public enum DayOfWeekEnum {
  MONDAY,
  TUESDAY,
  WEDNESDAY,
  THURSDAY,
  FRIDAY,
  SATURDAY,
  SUNDAY;

  public static DayOfWeekEnum from(DayOfWeek dayOfWeek) {
    return DayOfWeekEnum.valueOf(dayOfWeek.name());
  }

  public DayOfWeek toJavaDayOfWeek() {
    return DayOfWeek.valueOf(this.name());
  }

  public static DayOfWeekEnum fromJavaDayOfWeek(DayOfWeek day) {
    return DayOfWeekEnum.valueOf(day.name());
  }
}
