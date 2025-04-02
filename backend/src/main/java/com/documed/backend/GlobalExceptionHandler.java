package com.documed.backend;

import com.documed.backend.auth.exceptions.AuthServiceException;
import com.documed.backend.auth.exceptions.InvalidCredentialsException;
import com.documed.backend.auth.exceptions.UserAlreadyExistsException;
import com.documed.backend.auth.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(UserAlreadyExistsException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
    logger.error("User already exists: ", ex);
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(UserNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
    logger.error("User not found: ", ex);
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ResponseEntity<String> handleInvalidCredentialsException(InvalidCredentialsException ex) {
    logger.error("Invalid credentials: ", ex);
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(AuthServiceException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<String> handleAuthServiceException(AuthServiceException ex) {
    logger.error("Authentication service error: ", ex);
    return new ResponseEntity<>(
        "An internal server error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public void handleException(Exception ex) {
    logger.error("An unexpected error occurred: ", ex);
  }
}
