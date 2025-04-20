package com.documed.backend.services;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.*;

@Data
@Builder
public class Service {
  private int id;

  @Schema(required = true)
  @NonNull private String name;

  @Schema(required = true)
  @NonNull private BigDecimal price;

  @Schema(required = true)
  @NonNull private ServiceType type;

  @Schema(required = true)
  private int estimatedTime;
}
