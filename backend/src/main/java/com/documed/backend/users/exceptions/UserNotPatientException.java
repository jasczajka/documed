package com.documed.backend.users.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "User is not a patient")
public class UserNotPatientException extends RuntimeException {
  public UserNotPatientException(String message) {
    super(message);
  }
}
