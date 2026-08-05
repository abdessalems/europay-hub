package com.europay.hub.features.merchant.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

/**
 * Request to create a new API key.
 *
 * @param name      a human-friendly label (e.g. "Production server")
 * @param expiresAt optional expiry; {@code null} means the key never expires
 */
public record CreateApiKeyRequest(

        @NotBlank
        @Size(max = 100)
        String name,

        Instant expiresAt) {
}
