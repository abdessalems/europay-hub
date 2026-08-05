package com.europay.hub.features.iam.domain;

import com.europay.hub.shared.exception.BusinessRuleViolationException;

/** Raised when registering with an email that already exists. Maps to HTTP 409. */
public class EmailAlreadyInUseException extends BusinessRuleViolationException {

    public EmailAlreadyInUseException(String email) {
        super("EMAIL_ALREADY_IN_USE", "Email is already registered: " + email);
    }
}
