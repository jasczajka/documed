package com.documed.backend.users.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum AccountStatus {
  ACTIVE,
  PENDING_CONFIRMATION,
  DEACTIVATED
}
