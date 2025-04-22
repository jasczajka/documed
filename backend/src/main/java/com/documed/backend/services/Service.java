package com.documed.backend.services;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.*;

@Data
@Builder
public class Service {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private int id;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private String name;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private BigDecimal price;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private ServiceType type;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private int estimatedTime;
}
