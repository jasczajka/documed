package com.documed.backend.users.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Only doctors can have specializations")
public class SpecializationToNonDoctorException extends RuntimeException {
  public SpecializationToNonDoctorException(String message) {
    super(message);
  }
}
