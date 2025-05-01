package com.documed.backend.auth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Invalid OTP")
public class InvalidOtpException extends RuntimeException {
  public InvalidOtpException(String message) {
    super(message);
  }
}
