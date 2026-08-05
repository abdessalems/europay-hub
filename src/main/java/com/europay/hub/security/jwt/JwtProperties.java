package com.europay.hub.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT settings bound from {@code europay.security.jwt.*}.
 *
 * @param secret            HMAC secret (min 32 bytes for HS256); supply via env in production
 * @param expirationMinutes access-token lifetime in minutes
 * @param issuer            expected/emitted {@code iss} claim
 */
@ConfigurationProperties(prefix = "europay.security.jwt")
public record JwtProperties(String secret, long expirationMinutes, String issuer) {
}
