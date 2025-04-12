package com.documed.backend.services;

import java.math.BigDecimal;

import lombok.*;

@Data
@Builder
public class Service {
  private int id;
  @NonNull private String name;
  @NonNull private BigDecimal price;
  @NonNull private ServiceType type;
  private int estimatedTime;
}
