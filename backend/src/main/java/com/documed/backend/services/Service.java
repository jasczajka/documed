package com.documed.backend.services;

import com.documed.backend.additionalservices.AdditionalService;
import com.documed.backend.visits.Visit;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
@Data
public class Service {
  private int id;
  @NonNull private String name;
  @NonNull private BigDecimal price;
  @NonNull private ServiceType type;
  private int estimatedTime;
  private List<Visit> visits;
  private List<AdditionalService> additionalServices;

  public Service(int id, @NonNull String name, @NonNull BigDecimal price, @NonNull ServiceType type, int estimatedTime) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.type = type;
    this.estimatedTime = estimatedTime;
  }

  public Service(@NonNull String name, @NonNull BigDecimal price, @NonNull ServiceType type, int estimatedTime) {
    this.name = name;
    this.price = price;
    this.type = type;
    this.estimatedTime = estimatedTime;
  }

}
