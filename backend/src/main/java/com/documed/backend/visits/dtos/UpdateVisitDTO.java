package com.documed.backend.visits.dtos;

import lombok.Data;

@Data
public class UpdateVisitDTO {
  private String interview;
  private String diagnosis;
  private String recommendations;
}
