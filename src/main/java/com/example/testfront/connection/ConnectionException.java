package com.example.testfront.connection;

import org.springframework.http.HttpStatus;

public class ConnectionException extends RuntimeException {
    private final HttpStatus status;

    public ConnectionException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() { return status; }
}
