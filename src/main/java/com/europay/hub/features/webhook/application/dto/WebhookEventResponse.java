package com.europay.hub.features.webhook.application.dto;

import com.europay.hub.features.webhook.domain.WebhookEvent;
import java.time.Instant;
import java.util.UUID;

public record WebhookEventResponse(
        UUID id,
        String eventType,
        String status,
        int attempts,
        Integer lastStatusCode,
        UUID paymentId,
        Instant createdAt) {

    public static WebhookEventResponse from(WebhookEvent e) {
        return new WebhookEventResponse(
                e.id(), e.eventType(), e.status().name(), e.attempts(), e.lastStatusCode(), e.paymentId(), e.createdAt());
    }
}
