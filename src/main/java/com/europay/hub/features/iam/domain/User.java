package com.europay.hub.features.iam.domain;

import com.europay.hub.shared.domain.AggregateRoot;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A dashboard user who authenticates with email + password and receives a JWT.
 * A MERCHANT user belongs to exactly one merchant; an ADMIN user has no merchant.
 * The password is only ever held as a hash — the domain never sees plaintext.
 */
public class User extends AggregateRoot<UUID> {

    private final UUID id;
    private final UUID merchantId;
    private final String email;
    private final String passwordHash;
    private final Role role;
    private final UserStatus status;
    private final Instant createdAt;

    public User(UUID id, UUID merchantId, String email, String passwordHash,
                Role role, UserStatus status, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.merchantId = merchantId;
        this.email = normalizeEmail(email);
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash");
        this.role = Objects.requireNonNull(role, "role");
        this.status = Objects.requireNonNull(status, "status");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    /** Register a new active MERCHANT user for the given merchant. */
    public static User registerMerchantUser(UUID merchantId, String email, String passwordHash) {
        Objects.requireNonNull(merchantId, "merchantId");
        return new User(UUID.randomUUID(), merchantId, email, passwordHash,
                Role.MERCHANT, UserStatus.ACTIVE, Instant.now());
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    private static String normalizeEmail(String email) {
        Objects.requireNonNull(email, "email");
        return email.trim().toLowerCase();
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

    public String passwordHash() {
        return passwordHash;
    }

    public Role role() {
        return role;
    }

    public UserStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
