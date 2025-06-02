package com.documed.backend.prescriptions.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreatePrescriptionDTO {

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull() private LocalDate expirationDate;
}
