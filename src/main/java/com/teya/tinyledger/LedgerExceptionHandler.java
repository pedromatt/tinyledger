package com.teya.tinyledger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Map;

@ControllerAdvice
public class LedgerExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException exception) {
     Map<String, Object> body = Map.of(
             "timestamp", LocalDateTime.now(),
             "status", 400,
             "error", exception.getMessage()
     );
     return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
