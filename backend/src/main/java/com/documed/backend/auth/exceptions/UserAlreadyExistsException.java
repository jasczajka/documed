package com.documed.backend.auth.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
  public UserAlreadyExistsException(String message) {
    super(message);
  }
}
