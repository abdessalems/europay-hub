package com.europay.hub.features.customer.domain;

import com.europay.hub.shared.domain.AggregateRoot;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** An end payer belonging to a merchant. Unique per (merchant, email). */
public class Customer extends AggregateRoot<UUID> {

    private final UUID id;
    private final UUID merchantId;
    private final String email;
    private final String fullName;
    private final Instant createdAt;

    public Customer(UUID id, UUID merchantId, String email, String fullName, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.merchantId = Objects.requireNonNull(merchantId, "merchantId");
        this.email = normalizeEmail(email);
        this.fullName = requireText(fullName, "fullName");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public static Customer register(UUID merchantId, String email, String fullName) {
        return new Customer(UUID.randomUUID(), merchantId, email, fullName, Instant.now());
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

    public UUID merchantId() {
        return merchantId;
    }

    public String email() {
        return email;
    }

    public String fullName() {
        return fullName;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
