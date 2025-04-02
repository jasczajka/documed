package com.documed.backend.auth.exceptions;

public class InvalidCredentialsException extends RuntimeException {
  public InvalidCredentialsException(String message) {
    super(message);
  }
}
