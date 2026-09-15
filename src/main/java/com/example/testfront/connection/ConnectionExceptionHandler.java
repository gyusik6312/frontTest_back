package com.example.testfront.connection;

import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = UserConnectionController.class)
public class ConnectionExceptionHandler {
    @ExceptionHandler(ConnectionException.class)
    public ResponseEntity<Map<String, String>> handle(ConnectionException exception) {
        return ResponseEntity.status(exception.getStatus()).body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleConflict(DataIntegrityViolationException exception) {
        return ResponseEntity.status(409).body(Map.of("message", "이미 연결되어 있거나 사용자 정보가 변경되어 연결할 수 없습니다."));
    }
}
