package com.documed.backend.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class Specialization {
  private int id;
  @NonNull private String name;
}
