package com.documed.backend.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequestDTO {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Old password is required") private String oldPassword;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "New password is required") private String newPassword;
}
