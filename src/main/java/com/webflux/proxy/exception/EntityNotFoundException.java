package com.webflux.proxy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EntityNotFoundException extends ResponseStatusException {

    public EntityNotFoundException(HttpStatus status) {
        super(status);
    }

    public EntityNotFoundException(HttpStatus status, String reason) {
        super(status, reason);
    }

    public EntityNotFoundException(HttpStatus status, String reason, Throwable cause) {
        super(status, reason, cause);
    }

    public EntityNotFoundException(int rawStatusCode, String reason, Throwable cause) {
        super(rawStatusCode, reason, cause);
    }
}
