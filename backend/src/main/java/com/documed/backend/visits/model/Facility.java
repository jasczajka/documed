package com.documed.backend.visits.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class Facility {
  private int id;
  @NonNull private String address;
  @NonNull private String city;
  private List<Visit> visits;
}
