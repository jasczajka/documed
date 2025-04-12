package com.documed.backend.medicines.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Medicine {
  @Schema(required = true)
  private String id;

  @Schema(required = true)
  private String name;

  @Schema(required = true)
  private String commonName;

  @Schema(required = true)
  private String dosage;
}
