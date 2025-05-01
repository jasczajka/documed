package com.documed.backend.auth.model;

import com.documed.backend.users.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class CurrentUser {
  private final Integer userId;
  private final UserRole role;
}
