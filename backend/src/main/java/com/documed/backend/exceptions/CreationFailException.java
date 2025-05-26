package com.documed.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class CreationFailException extends RuntimeException {
  public CreationFailException(String message) {
    super(message);
  }
}
