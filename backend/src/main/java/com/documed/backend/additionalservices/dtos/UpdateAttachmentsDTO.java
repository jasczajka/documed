package com.documed.backend.additionalservices.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateAttachmentsDTO {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull() private List<Integer> attachmentIds;
}
