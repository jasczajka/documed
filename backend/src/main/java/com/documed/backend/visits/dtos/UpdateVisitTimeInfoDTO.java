package com.documed.backend.visits.dtos;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Data;

@Data
public class UpdateVisitTimeInfoDTO {
  private LocalDate date;
  private LocalTime startTime;
  private LocalTime endTime;
}
