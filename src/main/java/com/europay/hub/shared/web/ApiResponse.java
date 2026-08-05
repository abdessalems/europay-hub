package com.europay.hub.shared.web;

import java.time.Instant;

/**
 * Uniform envelope for every API response. Entities are never exposed directly — only
 * DTOs wrapped here. On success {@code error} is {@code null}; on failure {@code data} is.
 *
 * @param <T> the DTO payload type
 */
public record ApiResponse<T>(
        boolean success,
        T data,
        ErrorResponse error,
        Instant timestamp) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null, Instant.now());
    }

    public static <T> ApiResponse<T> fail(ErrorResponse error) {
        return new ApiResponse<>(false, null, error, Instant.now());
    }
}
