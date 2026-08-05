package com.europay.hub.features.merchant.domain;

import com.europay.hub.shared.domain.AggregateRoot;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A business that accepts payments through EuroPay Hub. Root of the merchant context;
 * owns API keys and (later) webhook configuration.
 */
public class Merchant extends AggregateRoot<UUID> {

    private final UUID id;
    private final String legalName;
    private final String email;
    private MerchantStatus status;
    private final Instant createdAt;

    public Merchant(UUID id, String legalName, String email, MerchantStatus status, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.legalName = requireText(legalName, "legalName");
        this.email = normalizeEmail(email);
        this.status = Objects.requireNonNull(status, "status");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    /** Register a new active merchant. */
    public static Merchant register(String legalName, String email) {
        return new Merchant(UUID.randomUUID(), legalName, email, MerchantStatus.ACTIVE, Instant.now());
    }

    public boolean isActive() {
        return status == MerchantStatus.ACTIVE;
    }

    public void suspend() {
        this.status = MerchantStatus.SUSPENDED;
    }

    private static String normalizeEmail(String email) {
        return requireText(email, "email").toLowerCase();
    }

    private static String requireText(String value, String field) {
        Objects.requireNonNull(value, field);
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return trimmed;
    }

    @Override
    public UUID id() {
        return id;
    }

    public String legalName() {
        return legalName;
    }

    public String email() {
        return email;
    }

    public MerchantStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
