package com.documed.backend.users.model;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Builder
@Data
public class Subscription {
  private int id;
  @NonNull private String name;
  @NonNull private BigDecimal price;
}
