package com.documed.backend.notifications;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum NotificationType {
  VISIT_TOMORROW,
  NEW_VISIT_INFO
}
