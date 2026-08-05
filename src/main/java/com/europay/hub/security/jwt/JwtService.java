package com.europay.hub.security.jwt;

import com.europay.hub.features.iam.domain.Role;
import com.europay.hub.features.iam.domain.User;
import com.europay.hub.security.SecurityUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

/**
 * Issues and validates HS256 JWTs. The token carries everything needed to build a
 * {@link SecurityUser} so request authentication needs no database round-trip.
 */
@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMinutes;
    private final String issuer;

    public JwtService(JwtProperties properties) {
        this.key = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = properties.expirationMinutes();
        this.issuer = properties.issuer();
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plus(expirationMinutes, ChronoUnit.MINUTES);
        var builder = Jwts.builder()
                .issuer(issuer)
                .subject(user.id().toString())
                .claim("email", user.email())
                .claim("role", user.role().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(key);
        if (user.merchantId() != null) {
            builder.claim("merchantId", user.merchantId().toString());
        }
        return builder.compact();
    }

    /** Parse and verify a token, returning the principal. Throws on invalid/expired tokens. */
    public SecurityUser parse(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        UUID userId = UUID.fromString(claims.getSubject());
        String merchantId = claims.get("merchantId", String.class);
        return new SecurityUser(
                userId,
                merchantId == null ? null : UUID.fromString(merchantId),
                claims.get("email", String.class),
                Role.valueOf(claims.get("role", String.class)));
    }

    public long expirationSeconds() {
        return expirationMinutes * 60;
    }
}
