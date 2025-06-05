package com.documed.backend.visits.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Feedback {
  private int id;
  private final int rating;
  private final String text;
  private final int visitId;
}
