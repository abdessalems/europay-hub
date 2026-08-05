package com.europay.hub.shared.exception;

/** Raised when a requested resource does not exist (or is not visible to the caller). Maps to HTTP 404. */
public class ResourceNotFoundException extends DomainException {

    public ResourceNotFoundException(String resource, Object id) {
        super("NOT_FOUND", resource + " not found: " + id);
    }
}
