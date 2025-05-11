package com.documed.backend.attachments.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenerateUploadUrlRequestDTO {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @Positive @NotNull(message = "Rozmiar pliku jest wymagany") private Long fileSizeBytes;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "Nazwa pliku jest wymagana") private String fileName;

  private Integer visitId;

  private Integer additionalServiceId;
}
