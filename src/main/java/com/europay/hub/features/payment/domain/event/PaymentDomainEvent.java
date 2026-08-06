package com.europay.hub.features.payment.domain.event;

import com.europay.hub.features.payment.domain.Payment;
import java.time.Instant;
import java.util.UUID;

/**
 * Published when a payment changes state. Consumed (in the same transaction) by the webhook
 * outbox and, later, the audit log — keeping those concerns decoupled from the payment code.
 *
 * @param eventType wire name, e.g. {@code payment.success}
 */
public record PaymentDomainEvent(
        UUID merchantId,
        UUID paymentId,
        UUID orderId,
        String eventType,
        long amountMinor,
        String currency,
        String status,
        String method,
        String providerReference,
        Instant occurredAt) {

    public static PaymentDomainEvent of(Payment payment, String eventType) {
        return new PaymentDomainEvent(
                payment.merchantId(),
                payment.id(),
                payment.orderId(),
                eventType,
                payment.amount().amountMinor(),
                payment.amount().currency().name(),
                payment.status().name(),
                payment.method().name(),
                payment.providerReference(),
                Instant.now());
    }
}
