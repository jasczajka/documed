package com.documed.backend.users.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum UserRole {
  PATIENT,
  DOCTOR,
  NURSE,
  WARD_CLERK,
  ADMINISTRATOR
}
