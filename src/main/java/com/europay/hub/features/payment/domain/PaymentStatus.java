package com.europay.hub.features.payment.domain;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Payment lifecycle states and the legal transitions between them. This is the heart of the
 * domain: the transition table makes illegal moves impossible rather than merely discouraged.
 *
 * <pre>
 * CREATED ─▶ PENDING ─▶ AUTHORIZED ─▶ SUCCESS ─▶ SETTLED
 *                │           │           └─▶ REFUNDED
 *                ├─▶ FAILED ─▶ (retry) PENDING
 *                ├─▶ EXPIRED
 *                └─▶ CANCELLED
 * </pre>
 */
public enum PaymentStatus {
    CREATED,
    PENDING,
    AUTHORIZED,
    SUCCESS,
    FAILED,
    EXPIRED,
    CANCELLED,
    REFUNDED,
    SETTLED;

    private static final Map<PaymentStatus, Set<PaymentStatus>> ALLOWED = new EnumMap<>(PaymentStatus.class);

    static {
        ALLOWED.put(CREATED, EnumSet.of(PENDING, CANCELLED));
        ALLOWED.put(PENDING, EnumSet.of(AUTHORIZED, SUCCESS, FAILED, EXPIRED, CANCELLED));
        ALLOWED.put(AUTHORIZED, EnumSet.of(SUCCESS, CANCELLED, FAILED));
        ALLOWED.put(SUCCESS, EnumSet.of(REFUNDED, SETTLED));
        ALLOWED.put(SETTLED, EnumSet.of(REFUNDED));
        ALLOWED.put(FAILED, EnumSet.of(PENDING));
        ALLOWED.put(EXPIRED, EnumSet.noneOf(PaymentStatus.class));
        ALLOWED.put(CANCELLED, EnumSet.noneOf(PaymentStatus.class));
        ALLOWED.put(REFUNDED, EnumSet.noneOf(PaymentStatus.class));
    }

    public boolean canTransitionTo(PaymentStatus target) {
        return ALLOWED.get(this).contains(target);
    }

    public boolean isTerminal() {
        return ALLOWED.get(this).isEmpty();
    }
}
