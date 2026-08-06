package com.europay.hub.features.payment.application.dto;

import com.europay.hub.features.payment.domain.Payment;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID orderId,
        String paymentMethod,
        BigDecimal amount,
        String currency,
        String status,
        String providerReference,
        String failureReason,
        Instant createdAt) {

    public static PaymentResponse from(Payment p) {
        return new PaymentResponse(
                p.id(),
                p.orderId(),
                p.method().name(),
                p.amount().toMajor(),
                p.amount().currency().name(),
                p.status().name(),
                p.providerReference(),
                p.failureReason(),
                p.createdAt());
    }
}
