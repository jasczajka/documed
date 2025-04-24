package com.documed.backend.auth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "OTP validation failed")
public class OtpException extends RuntimeException {
  public OtpException(String message) {
    super(message);
  }
}
