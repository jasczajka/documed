package com.documed.backend.visits.dtos;

import com.documed.backend.visits.model.VisitStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class VisitDTO {
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

  private String patientInformation;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private int patientId;
}
