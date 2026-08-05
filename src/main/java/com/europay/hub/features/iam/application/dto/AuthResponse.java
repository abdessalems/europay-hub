package com.europay.hub.features.iam.application.dto;

/**
 * Successful authentication result.
 *
 * @param accessToken      the signed JWT
 * @param tokenType        always {@code Bearer}
 * @param expiresInSeconds token lifetime
 * @param role             the authenticated user's role
 */
public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        String role) {
}
