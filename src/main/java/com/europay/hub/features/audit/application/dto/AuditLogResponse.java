package com.europay.hub.features.audit.application.dto;

import com.europay.hub.features.audit.domain.AuditLog;
import java.time.Instant;
import java.util.UUID;

public record AuditLogResponse(
        UUID id,
        String actor,
        String action,
        String entityType,
        UUID entityId,
        String metadata,
        Instant createdAt) {

    public static AuditLogResponse from(AuditLog a) {
        return new AuditLogResponse(
                a.id(), a.actor(), a.action(), a.entityType(), a.entityId(), a.metadata(), a.createdAt());
    }
}
