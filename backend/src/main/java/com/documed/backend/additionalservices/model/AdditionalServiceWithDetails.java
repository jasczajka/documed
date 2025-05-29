package com.documed.backend.additionalservices.model;

import com.documed.backend.attachments.dtos.FileInfoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class AdditionalServiceWithDetails {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private int id;

  private String description;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private LocalDate date;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private Integer fulfillerId;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String fulfillerFullName;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private Integer patientId;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String patientFullName;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private Integer serviceId;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String serviceName;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private List<FileInfoDTO> attachments;
}
