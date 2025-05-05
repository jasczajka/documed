package com.documed.backend.prescriptions.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Amount can't be smaller than 0")
public class WrongAmountException extends RuntimeException {
  public WrongAmountException(String message) {
    super(message);
  }
}
