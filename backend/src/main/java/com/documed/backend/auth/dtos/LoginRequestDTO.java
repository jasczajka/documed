package com.documed.backend.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequestDTO {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Login (email/PESEL) jest wymagany")
  private String login;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Hasło jest wymagane") private String password;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "Placówka jest wymagana") private Integer facilityId;
}
