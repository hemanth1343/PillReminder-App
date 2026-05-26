package com.pillreminder.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        Map<String, String> fieldErrors
) {
    public static ErrorResponse of(org.springframework.http.HttpStatus status, String message) {
        return new ErrorResponse(LocalDateTime.now(), status.value(),
                status.getReasonPhrase(), message, null);
    }

    public static ErrorResponse ofFields(org.springframework.http.HttpStatus status,
                                         String message,
                                         Map<String, String> fieldErrors) {
        return new ErrorResponse(LocalDateTime.now(), status.value(),
                status.getReasonPhrase(), message, fieldErrors);
    }
}
