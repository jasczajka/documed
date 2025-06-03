package com.documed.backend.users.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SubscriptionToService {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private final int subscriptionId;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private final int serviceId;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private int discount;
}
