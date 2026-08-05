package com.europay.hub.shared.web;

import java.util.List;

/**
 * Structured error payload returned inside {@link ApiResponse} for any failed request.
 *
 * @param code    stable machine-readable error code
 * @param message human-readable summary
 * @param path    the request path that produced the error
 * @param details field-level violations (for validation errors); empty otherwise
 */
public record ErrorResponse(
        String code,
        String message,
        String path,
        List<FieldViolation> details) {

    public record FieldViolation(String field, String message) {
    }

    public static ErrorResponse of(String code, String message, String path) {
        return new ErrorResponse(code, message, path, List.of());
    }

    public static ErrorResponse of(String code, String message, String path, List<FieldViolation> details) {
        return new ErrorResponse(code, message, path, details);
    }
}
