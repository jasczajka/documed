package com.documed.backend.additionalservices.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateDescriptionDTO {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank() private String description;
}
