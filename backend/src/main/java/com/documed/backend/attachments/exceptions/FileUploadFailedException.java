package com.documed.backend.attachments.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class FileUploadFailedException extends RuntimeException {
  public FileUploadFailedException(String message) {
    super(message);
  }
}
