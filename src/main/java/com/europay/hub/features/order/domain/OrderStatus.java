package com.europay.hub.features.order.domain;

/**
 * Order lifecycle. An order starts {@code CREATED}; it becomes {@code PAID} when its payment
 * succeeds (Phase 4, via a domain event), or {@code CANCELLED} if the merchant cancels it.
 * {@code EXPIRED} is reserved for time-limited orders.
 */
public enum OrderStatus {
    CREATED,
    PAID,
    CANCELLED,
    EXPIRED
}
