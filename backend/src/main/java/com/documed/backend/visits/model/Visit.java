package com.documed.backend.visits.model;

import java.math.BigDecimal;
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
}
