package com.europay.hub.shared.exception;

/**
 * Thrown when an operation violates a business rule (e.g. refunding a non-successful
 * payment, approving an expired payment). Maps to HTTP 409 Conflict.
 */
public class BusinessRuleViolationException extends DomainException {

    public BusinessRuleViolationException(String message) {
        super("BUSINESS_RULE_VIOLATION", message);
    }

    public BusinessRuleViolationException(String code, String message) {
        super(code, message);
    }
}
