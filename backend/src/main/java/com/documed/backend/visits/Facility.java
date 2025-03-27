package com.documed.backend.visits;

import java.util.List;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Facility {
  private int id;
  @NonNull private String address;
  @NonNull private String city;
  private List<Visit> visits;

  public Facility(int id, @NonNull String address, @NonNull String city) {
    this.id = id;
    this.address = address;
    this.city = city;
  }
}
