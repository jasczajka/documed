package com.documed.backend.users.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class Specialization {
  private int id;
  @NonNull private String name;
}
