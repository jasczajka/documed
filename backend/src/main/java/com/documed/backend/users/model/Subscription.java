package com.documed.backend.users.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Builder
@Data
public class Subscription {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private int id;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private String name;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private BigDecimal price;
}
