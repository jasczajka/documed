package com.documed.backend.visits;

import lombok.Data;

@Data
public class Feedback {
  private int id;
  private final int rating;
  private final String text;
  private final Visit visit;
}
