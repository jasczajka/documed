package com.documed.backend.prescriptions.model;

import com.documed.backend.prescriptions.PrescriptionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Prescription {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private int id;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private int accessCode;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private final LocalDate date;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private LocalDate expirationDate;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private PrescriptionStatus status;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String issuingDoctorFullName;
}
