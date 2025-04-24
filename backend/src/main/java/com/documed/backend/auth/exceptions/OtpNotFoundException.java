package com.documed.backend.auth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "OTP not found or invalid")
public class OtpNotFoundException extends OtpException {
  public OtpNotFoundException(String message) {
    super(message);
  }
}
