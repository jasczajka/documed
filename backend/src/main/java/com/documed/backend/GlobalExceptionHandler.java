package com.documed.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public void handleGenericException(Exception ex) throws Exception {
    ResponseStatus responseStatus =
        AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
    if (responseStatus != null) {
      throw ex;
    }
    logger.error("An unexpected error occurred: ", ex);
  }
}
