package com.documed.backend.visits.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Internal server error")
public class CancelVisitException extends RuntimeException {
  public CancelVisitException(String message) {
    super(message);
  }
}
