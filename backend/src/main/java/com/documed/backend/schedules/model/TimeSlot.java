package com.documed.backend.schedules.model;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class TimeSlot {
  private int id;
  private int visitId;
  private final int doctorId;
  private final int facilityId;
  private final LocalTime startTime;
  private final LocalTime endTime;
  private final LocalDate date;
  private boolean isBusy;
}
