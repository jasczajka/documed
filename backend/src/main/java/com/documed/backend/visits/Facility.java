package com.documed.backend.visits;

import java.util.List;
import lombok.Data;
import lombok.NonNull;

@Data
public class Facility {
  private final int id;
  @NonNull private String address;
  @NonNull private String city;
  private List<Visit> visits;
}
