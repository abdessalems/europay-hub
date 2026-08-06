package com.europay.hub.features.webhook.domain;

import com.europay.hub.shared.domain.AggregateRoot;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** One delivery attempt for a {@link WebhookEvent}. */
public class WebhookDelivery extends AggregateRoot<UUID> {

    private final UUID id;
    private final UUID webhookEventId;
    private final int attempt;
    private final Integer statusCode;
    private final boolean success;
    private final String error;
    private final Instant createdAt;

    public WebhookDelivery(UUID id, UUID webhookEventId, int attempt, Integer statusCode,
                           boolean success, String error, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.webhookEventId = Objects.requireNonNull(webhookEventId, "webhookEventId");
        this.attempt = attempt;
        this.statusCode = statusCode;
        this.success = success;
        this.error = error;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public static WebhookDelivery record(UUID webhookEventId, int attempt, Integer statusCode,
                                         boolean success, String error) {
        return new WebhookDelivery(UUID.randomUUID(), webhookEventId, attempt, statusCode, success, error, Instant.now());
    }

    @Override
    public UUID id() {
        return id;
    }

    public UUID webhookEventId() {
        return webhookEventId;
    }

    public int attempt() {
        return attempt;
    }

    public Integer statusCode() {
        return statusCode;
    }

    public boolean success() {
        return success;
    }

    public String error() {
        return error;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
