package com.europay.hub.features.merchant.application.dto;

import com.europay.hub.features.merchant.domain.ApiKey;
import java.time.Instant;
import java.util.UUID;

/**
 * Returned once, at creation time. {@code secretKey} is the full plaintext key and is never
 * retrievable again — the merchant must store it now.
 */
public record ApiKeyCreatedResponse(
        UUID id,
        String name,
        String prefix,
        String secretKey,
        String status,
        Instant createdAt,
        Instant expiresAt) {

    public static ApiKeyCreatedResponse from(ApiKey k, String secretKey) {
        return new ApiKeyCreatedResponse(
                k.id(), k.name(), k.keyPrefix(), secretKey, k.status().name(), k.createdAt(), k.expiresAt());
    }
}
