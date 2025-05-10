package com.documed.backend.attachments.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompleteUploadRequestDTO {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "Id załącznika jest wymagane") private Integer attachmentId;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "Klucz S3 jest wymagany") private String s3Key;
}
