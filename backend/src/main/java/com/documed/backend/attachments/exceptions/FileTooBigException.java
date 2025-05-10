package com.documed.backend.attachments.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class FileTooBigException extends RuntimeException {
  public FileTooBigException(String message) {
    super(message);
  }
}
