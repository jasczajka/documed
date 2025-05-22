package com.documed.backend.additionalservices.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class AdditionalServiceReturnDTO {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private int id;

  private String description;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private Date date;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private Integer fulfillerId;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private Integer patientId;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private Integer serviceId;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private List<String> attachmentUrls;
}
