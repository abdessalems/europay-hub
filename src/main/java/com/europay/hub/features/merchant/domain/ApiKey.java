package com.europay.hub.features.merchant.domain;

import com.europay.hub.shared.domain.AggregateRoot;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A merchant's API credential for server-to-server calls. The secret itself is shown to
 * the merchant exactly once at creation; only a {@code keyPrefix} (for identification) and
 * a {@code keyHash} (for verification) are stored — never the plaintext.
 */
public class ApiKey extends AggregateRoot<UUID> {

    private final UUID id;
    private final UUID merchantId;
    private final String name;
    private final String keyPrefix;
    private final String keyHash;
    private ApiKeyStatus status;
    private final Instant createdAt;
    private Instant lastUsedAt;
    private final Instant expiresAt;

    public ApiKey(UUID id, UUID merchantId, String name, String keyPrefix, String keyHash,
                  ApiKeyStatus status, Instant createdAt, Instant lastUsedAt, Instant expiresAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.merchantId = Objects.requireNonNull(merchantId, "merchantId");
        this.name = Objects.requireNonNull(name, "name");
        this.keyPrefix = Objects.requireNonNull(keyPrefix, "keyPrefix");
        this.keyHash = Objects.requireNonNull(keyHash, "keyHash");
        this.status = Objects.requireNonNull(status, "status");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.lastUsedAt = lastUsedAt;
        this.expiresAt = expiresAt;
    }

    /** Issue a fresh, active API key. {@code expiresAt} may be {@code null} (never expires). */
    public static ApiKey issue(UUID merchantId, String name, String keyPrefix, String keyHash, Instant expiresAt) {
        return new ApiKey(UUID.randomUUID(), merchantId, name, keyPrefix, keyHash,
                ApiKeyStatus.ACTIVE, Instant.now(), null, expiresAt);
    }

    /** Usable when active and not past its expiry. */
    public boolean isUsable(Instant now) {
        return status == ApiKeyStatus.ACTIVE && (expiresAt == null || now.isBefore(expiresAt));
    }

    public void revoke() {
        this.status = ApiKeyStatus.REVOKED;
    }

    public void markUsed(Instant at) {
        this.lastUsedAt = at;
    }

    @Override
    public UUID id() {
        return id;
    }

    public UUID merchantId() {
        return merchantId;
    }

    public String name() {
        return name;
    }

    public String keyPrefix() {
        return keyPrefix;
    }

    public String keyHash() {
        return keyHash;
    }

    public ApiKeyStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant lastUsedAt() {
        return lastUsedAt;
    }

    public Instant expiresAt() {
        return expiresAt;
    }
}
