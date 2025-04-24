package com.documed.backend.auth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS, reason = "OTP attempts limit reached")
public class OtpLimitExceededException extends OtpException {
  public OtpLimitExceededException(String message) {
    super(message);
  }
}
