package com.documed.backend.users.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class Specialization {
  @Schema(required = true)
  private int id;

  @Schema(required = true)
  @NonNull private String name;
}
