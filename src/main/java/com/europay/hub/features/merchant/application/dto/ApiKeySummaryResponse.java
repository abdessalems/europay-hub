package com.europay.hub.features.merchant.application.dto;

import com.europay.hub.features.merchant.domain.ApiKey;
import java.time.Instant;
import java.util.UUID;

/** Safe view of an API key for listings — the secret is never included. */
public record ApiKeySummaryResponse(
        UUID id,
        String name,
        String prefix,
        String status,
        Instant createdAt,
        Instant lastUsedAt,
        Instant expiresAt) {

    public static ApiKeySummaryResponse from(ApiKey k) {
        return new ApiKeySummaryResponse(
                k.id(), k.name(), k.keyPrefix(), k.status().name(),
                k.createdAt(), k.lastUsedAt(), k.expiresAt());
    }
}
