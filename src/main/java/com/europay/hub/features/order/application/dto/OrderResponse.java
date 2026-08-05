package com.europay.hub.features.order.application.dto;

import com.europay.hub.features.order.domain.Order;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String reference,
        String status,
        BigDecimal amount,
        String currency,
        UUID customerId,
        Instant createdAt) {

    public static OrderResponse from(Order o) {
        return new OrderResponse(
                o.id(),
                o.reference(),
                o.status().name(),
                o.amount().toMajor(),
                o.amount().currency().name(),
                o.customerId(),
                o.createdAt());
    }
}
