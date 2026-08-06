package com.europay.hub.features.webhook.domain;

import com.europay.hub.shared.domain.AggregateRoot;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** A merchant's registered callback URL + signing secret. One per merchant. */
public class WebhookEndpoint extends AggregateRoot<UUID> {

    private final UUID id;
    private final UUID merchantId;
    private String url;
    private String secret;
    private boolean active;
    private final Instant createdAt;

    public WebhookEndpoint(UUID id, UUID merchantId, String url, String secret, boolean active, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.merchantId = Objects.requireNonNull(merchantId, "merchantId");
        this.url = Objects.requireNonNull(url, "url");
        this.secret = Objects.requireNonNull(secret, "secret");
        this.active = active;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public static WebhookEndpoint register(UUID merchantId, String url, String secret) {
        return new WebhookEndpoint(UUID.randomUUID(), merchantId, url, secret, true, Instant.now());
    }

    public void update(String url, String secret) {
        this.url = Objects.requireNonNull(url, "url");
        this.secret = Objects.requireNonNull(secret, "secret");
        this.active = true;
    }

    public void disable() {
        this.active = false;
    }

    @Override
    public UUID id() {
        return id;
    }

    public UUID merchantId() {
        return merchantId;
    }

    public String url() {
        return url;
    }

    public String secret() {
        return secret;
    }

    public boolean isActive() {
        return active;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
