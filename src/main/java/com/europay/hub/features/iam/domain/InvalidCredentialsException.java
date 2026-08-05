package com.europay.hub.features.iam.domain;

import com.europay.hub.shared.exception.DomainException;

/** Raised on a failed login (unknown email, wrong password, or disabled user). Maps to HTTP 401. */
public class InvalidCredentialsException extends DomainException {

    public InvalidCredentialsException() {
        super("INVALID_CREDENTIALS", "Invalid email or password");
    }
}
