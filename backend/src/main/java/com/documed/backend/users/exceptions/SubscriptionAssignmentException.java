package com.documed.backend.users.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "User is not a doctor")
public class SubscriptionAssignmentException extends RuntimeException {
  public SubscriptionAssignmentException(String message) {
    super(message);
  }
}
