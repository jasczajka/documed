package com.documed.backend.services;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Service {
  private int id;
  @NonNull private String name;
  @NonNull private BigDecimal price;
  @NonNull private ServiceType type;
  private int estimatedTime;

  public Service(
      @NonNull String name,
      @NonNull BigDecimal price,
      @NonNull ServiceType type,
      int estimatedTime) {
    this.name = name;
    this.price = price;
    this.type = type;
    this.estimatedTime = estimatedTime;
  }
}
