package com.documed.backend.prescriptions.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Internal server error")
public class PrescriptionServiceException extends RuntimeException {
  public PrescriptionServiceException(String message) {
    super(message);
  }
}
