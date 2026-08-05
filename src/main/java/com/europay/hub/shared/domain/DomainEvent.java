package com.europay.hub.shared.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * Marker contract for something meaningful that happened in the domain
 * (e.g. {@code PaymentAuthorized}). Published after the aggregate is persisted
 * and consumed by webhook dispatch, audit logging, and order updates.
 */
public interface DomainEvent {

    UUID eventId();

    Instant occurredAt();
}
