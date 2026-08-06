package com.europay.hub.shared.event;

import java.util.Map;
import java.util.UUID;

/**
 * A cross-cutting event any feature can publish to have an action recorded in the audit log.
 * Lives in the shared kernel so publishers don't depend on the audit feature.
 *
 * @param actor      who performed it, e.g. {@code user:<id>}, {@code api-key}, {@code system}
 * @param action     what happened, e.g. {@code MERCHANT_REGISTERED}, {@code API_KEY_CREATED}
 * @param entityType the affected entity type, e.g. {@code PAYMENT}
 * @param metadata   small extra context (serialized to JSON)
 */
public record AuditEvent(
        UUID merchantId,
        String actor,
        String action,
        String entityType,
        UUID entityId,
        Map<String, Object> metadata) {
}
