package com.documed.backend.additionalservices.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateAdditionalServiceDTO {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank() private String description;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull() private Date date;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull() private Integer fulfillerId;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull() private Integer patientId;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull() private Integer serviceId;

  private List<Integer> attachmentIds;
}
