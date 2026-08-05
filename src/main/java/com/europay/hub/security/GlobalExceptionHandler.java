package com.europay.hub.security;

import com.europay.hub.features.iam.domain.InvalidCredentialsException;
import com.europay.hub.shared.exception.BusinessRuleViolationException;
import com.europay.hub.shared.exception.DomainException;
import com.europay.hub.shared.exception.ResourceNotFoundException;
import com.europay.hub.shared.web.ApiResponse;
import com.europay.hub.shared.web.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Centralised, consistent error handling for the whole API. Every failure is returned
 * as an {@link ApiResponse} wrapping an {@link ErrorResponse}, so clients get one shape.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidCredentials(
            InvalidCredentialsException ex, HttpServletRequest request) {
        log.warn("Failed login attempt for {}", request.getRequestURI());
        return build(HttpStatus.UNAUTHORIZED, ex.code(), ex.getMessage(), request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("Not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, ex.code(), ex.getMessage(), request);
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessRule(
            BusinessRuleViolationException ex, HttpServletRequest request) {
        log.warn("Business rule violation: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, ex.code(), ex.getMessage(), request);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiResponse<Void>> handleDomain(
            DomainException ex, HttpServletRequest request) {
        log.warn("Domain error [{}]: {}", ex.code(), ex.getMessage());
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.code(), ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ErrorResponse.FieldViolation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(GlobalExceptionHandler::toViolation)
                .toList();
        ErrorResponse error = ErrorResponse.of(
                "VALIDATION_ERROR", "Request validation failed", request.getRequestURI(), violations);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.fail(error));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(
            Exception ex, HttpServletRequest request) {
        log.error("Unexpected error handling {}", request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "An unexpected error occurred", request);
    }

    private static ErrorResponse.FieldViolation toViolation(FieldError fieldError) {
        return new ErrorResponse.FieldViolation(fieldError.getField(), fieldError.getDefaultMessage());
    }

    private static ResponseEntity<ApiResponse<Void>> build(
            HttpStatus status, String code, String message, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.of(code, message, request.getRequestURI());
        return ResponseEntity.status(status).body(ApiResponse.fail(error));
    }
}
