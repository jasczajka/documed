package com.documed.backend.prescriptions.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Prescription is already issued")
public class AlreadyIssuedException extends RuntimeException {
  public AlreadyIssuedException(String message) {
    super(message);
  }
}
