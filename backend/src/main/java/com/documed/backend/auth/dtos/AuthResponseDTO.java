package com.documed.backend.auth.dtos;

import com.documed.backend.users.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDTO {

  private String token;
  private Integer userId;
  private UserRole role;
}
