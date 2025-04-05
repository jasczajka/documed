package com.documed.backend.auth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Internal error")
public class AuthServiceException extends RuntimeException {
  public AuthServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
