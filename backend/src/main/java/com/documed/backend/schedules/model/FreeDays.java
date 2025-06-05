package com.documed.backend.schedules.model;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FreeDays {
  private int id;
  private LocalDate startDate;
  private LocalDate endDate;
  private int userId;
}
