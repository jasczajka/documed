package com.documed.backend.visits.model;

import com.documed.backend.schedules.model.TimeSlot;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class Visit {
  private int id;
  @NonNull private VisitStatus status;
  private String interview;
  private String diagnosis;
  private String recommendations;
  private BigDecimal totalCost;
  private int facilityId;
  private int serviceId;
  private String patientInformation;
  private int patientId;
  private int doctorId;
  private List<TimeSlot> reservedTimeSlots;
  LocalDate date;
  LocalTime startTime;
  LocalTime endTime;
}
