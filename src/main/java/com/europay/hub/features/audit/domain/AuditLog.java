package com.europay.hub.features.audit.domain;

import com.europay.hub.shared.domain.AggregateRoot;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** An append-only audit record. */
public class AuditLog extends AggregateRoot<UUID> {

    private final UUID id;
    private final UUID merchantId;
    private final String actor;
    private final String action;
    private final String entityType;
    private final UUID entityId;
    private final String metadata;
    private final Instant createdAt;

    public AuditLog(UUID id, UUID merchantId, String actor, String action, String entityType,
                    UUID entityId, String metadata, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.merchantId = merchantId;
        this.actor = Objects.requireNonNull(actor, "actor");
        this.action = Objects.requireNonNull(action, "action");
        this.entityType = entityType;
        this.entityId = entityId;
        this.metadata = metadata;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public static AuditLog record(UUID merchantId, String actor, String action, String entityType,
                                  UUID entityId, String metadata) {
        return new AuditLog(UUID.randomUUID(), merchantId, actor, action, entityType, entityId, metadata, Instant.now());
    }

    @Override
    public UUID id() {
        return id;
    }

    public UUID merchantId() {
        return merchantId;
    }

    public String actor() {
        return actor;
    }

    public String action() {
        return action;
    }

    public String entityType() {
        return entityType;
    }

    public UUID entityId() {
        return entityId;
    }

    public String metadata() {
        return metadata;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
