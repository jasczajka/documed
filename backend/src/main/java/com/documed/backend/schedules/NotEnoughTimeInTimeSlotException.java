package com.documed.backend.schedules;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotEnoughTimeInTimeSlotException extends RuntimeException {
  public NotEnoughTimeInTimeSlotException(String message) {
    super(message);
  }
}
