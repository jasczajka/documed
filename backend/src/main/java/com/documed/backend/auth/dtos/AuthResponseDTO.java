package com.documed.backend.auth.dtos;

import com.documed.backend.users.model.UserRole;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class AuthResponseDTO {

  @NonNull private String token;
  @NonNull private Integer userId;
  @NonNull private UserRole role;
}
