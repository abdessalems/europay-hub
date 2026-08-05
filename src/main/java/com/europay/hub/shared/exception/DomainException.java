package com.europay.hub.shared.exception;

/**
 * Base type for all domain/business errors. Carries a stable machine-readable {@code code}
 * so the API can return consistent error responses independent of the message text.
 */
public abstract class DomainException extends RuntimeException {

    private final String code;

    protected DomainException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String code() {
        return code;
    }
}
