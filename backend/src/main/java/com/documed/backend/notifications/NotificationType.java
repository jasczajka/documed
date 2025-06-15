package com.documed.backend.notifications;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum NotificationType {
  VISIT_CANCELLATION,
  VISIT_REMINDER,
  ADDITIONAL_SERVICE_UPDATE,
}
