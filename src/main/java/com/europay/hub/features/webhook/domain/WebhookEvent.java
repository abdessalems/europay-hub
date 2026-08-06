package com.europay.hub.features.webhook.domain;

import com.europay.hub.shared.domain.AggregateRoot;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Outbox record for one webhook event. Created in the same transaction as the payment change,
 * then delivered asynchronously by the dispatcher with bounded retries and exponential backoff.
 */
public class WebhookEvent extends AggregateRoot<UUID> {

    private final UUID id;
    private final UUID merchantId;
    private final String eventType;
    private final UUID paymentId;
    private final String payload;
    private WebhookStatus status;
    private int attempts;
    private final int maxAttempts;
    private Instant nextAttemptAt;
    private Integer lastStatusCode;
    private String lastError;
    private final Instant createdAt;

    public WebhookEvent(UUID id, UUID merchantId, String eventType, UUID paymentId, String payload,
                        WebhookStatus status, int attempts, int maxAttempts, Instant nextAttemptAt,
                        Integer lastStatusCode, String lastError, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.merchantId = Objects.requireNonNull(merchantId, "merchantId");
        this.eventType = Objects.requireNonNull(eventType, "eventType");
        this.paymentId = paymentId;
        this.payload = Objects.requireNonNull(payload, "payload");
        this.status = Objects.requireNonNull(status, "status");
        this.attempts = attempts;
        this.maxAttempts = maxAttempts;
        this.nextAttemptAt = Objects.requireNonNull(nextAttemptAt, "nextAttemptAt");
        this.lastStatusCode = lastStatusCode;
        this.lastError = lastError;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public static WebhookEvent queue(UUID merchantId, String eventType, UUID paymentId, String payload) {
        return new WebhookEvent(UUID.randomUUID(), merchantId, eventType, paymentId, payload,
                WebhookStatus.PENDING, 0, 3, Instant.now(), null, null, Instant.now());
    }

    public void markDelivered(int statusCode) {
        this.attempts++;
        this.status = WebhookStatus.DELIVERED;
        this.lastStatusCode = statusCode;
        this.lastError = null;
    }

    /** Record a failed attempt; schedule the next one or give up after {@code maxAttempts}. */
    public void recordFailure(Integer statusCode, String error) {
        this.attempts++;
        this.lastStatusCode = statusCode;
        this.lastError = error;
        if (this.attempts >= this.maxAttempts) {
            this.status = WebhookStatus.FAILED;
        } else {
            // exponential backoff: 30s, 60s, 120s …
            long seconds = 30L * (1L << (this.attempts - 1));
            this.nextAttemptAt = Instant.now().plus(Duration.ofSeconds(seconds));
        }
    }

    @Override
    public UUID id() {
        return id;
    }

    public UUID merchantId() {
        return merchantId;
    }

    public String eventType() {
        return eventType;
    }

    public UUID paymentId() {
        return paymentId;
    }

    public String payload() {
        return payload;
    }

    public WebhookStatus status() {
        return status;
    }

    public int attempts() {
        return attempts;
    }

    public int maxAttempts() {
        return maxAttempts;
    }

    public Instant nextAttemptAt() {
        return nextAttemptAt;
    }

    public Integer lastStatusCode() {
        return lastStatusCode;
    }

    public String lastError() {
        return lastError;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
