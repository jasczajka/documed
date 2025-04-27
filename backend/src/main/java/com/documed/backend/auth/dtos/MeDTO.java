package com.documed.backend.auth.dtos;

import com.documed.backend.users.model.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
public class MeDTO {

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @Setter(AccessLevel.NONE)
  private int id;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private String firstName;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private String lastName;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private String email;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NonNull private UserRole role;
}
