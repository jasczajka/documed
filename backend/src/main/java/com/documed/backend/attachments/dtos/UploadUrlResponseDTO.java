package com.documed.backend.attachments.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploadUrlResponseDTO {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String uploadUrl;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String s3Key;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private Integer attachmentId;
}
