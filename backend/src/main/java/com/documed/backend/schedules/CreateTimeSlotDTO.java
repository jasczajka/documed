package com.documed.backend.schedules;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Data;

@Data
public class CreateTimeSlotDTO {
  private final int doctorId;
  private final LocalTime startTime;
  private final LocalTime endTime;
  private final LocalDate date;
}
