package com.documed.backend.visits.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class VisitWithDetails {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private int id;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private VisitStatus status;

  private String interview;
  private String diagnosis;
  private String recommendations;
  private BigDecimal totalCost;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private int facilityId;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private int serviceId;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String serviceName;

  private String patientInformation;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private int patientId;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String patientFullName;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String patientPesel;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private LocalDate patientBirthDate;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private int doctorId;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String doctorFullName;

  LocalDate date;

  LocalTime startTime;

  LocalTime endTime;
}
