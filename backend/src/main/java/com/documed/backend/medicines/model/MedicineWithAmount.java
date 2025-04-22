package com.documed.backend.medicines.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MedicineWithAmount {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String id;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String commonName;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String dosage;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private int amount;
}
