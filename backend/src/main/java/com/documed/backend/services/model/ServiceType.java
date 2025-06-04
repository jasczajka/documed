package com.documed.backend.services.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum ServiceType {
  REGULAR_SERVICE,
  ADDITIONAL_SERVICE
}
