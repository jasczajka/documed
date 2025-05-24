package com.documed.backend.visits.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
    value = HttpStatus.BAD_REQUEST,
    reason = "Visit is in wrong status to perform this operation")
public class WrongVisitStatusException extends RuntimeException {
  public WrongVisitStatusException(String message) {
    super(message);
  }
}
