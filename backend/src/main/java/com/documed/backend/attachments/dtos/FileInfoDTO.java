package com.documed.backend.attachments.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileInfoDTO {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private Integer id;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String downloadUrl;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "Nazwa pliku jest wymagana") private String fileName;
}
