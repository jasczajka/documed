package com.documed.backend.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResetPasswordRequestDTO {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull private String email;
}
