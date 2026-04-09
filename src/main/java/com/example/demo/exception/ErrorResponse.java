package com.example.demo.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@Schema(description = "Standard error response")
public record ErrorResponse(
        @Schema(description = "HTTP status code") int status,
        @Schema(description = "Error message") String message,
        @Schema(description = "Detailed validation errors") List<String> errors,
        @Schema(description = "Timestamp of the error") Instant timestamp
) {
    public static ErrorResponse of(int status, String message) {
        return new ErrorResponse(status, message, List.of(), Instant.now());
    }

    public static ErrorResponse of(int status, String message, List<String> errors) {
        return new ErrorResponse(status, message, errors, Instant.now());
    }
}
