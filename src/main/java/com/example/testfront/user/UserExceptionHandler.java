package com.example.testfront.user;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice(assignableTypes = UserController.class)
public class UserExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handle(ResponseStatusException exception) {
        return ResponseEntity.status(exception.getStatusCode())
                .body(Map.of("message", exception.getReason()));
    }
}
