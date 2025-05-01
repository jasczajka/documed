package com.documed.backend.auth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.GONE, reason = "OTP has expired")
public class OtpExpiredException extends OtpException {
  public OtpExpiredException(String message) {
    super(message);
  }
}
