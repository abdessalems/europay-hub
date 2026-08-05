package com.europay.hub.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.europay.hub.features.iam.domain.Role;
import com.europay.hub.features.iam.domain.User;
import com.europay.hub.features.iam.domain.UserStatus;
import com.europay.hub.security.SecurityUser;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("JwtService")
class JwtServiceTest {

    private final JwtService jwtService = new JwtService(new JwtProperties(
            "test-secret-test-secret-test-secret-0123456789", 60, "europay-hub"));

    private User sampleUser() {
        return new User(UUID.randomUUID(), UUID.randomUUID(), "merchant@example.com",
                "hash", Role.MERCHANT, UserStatus.ACTIVE, Instant.now());
    }

    @Test
    @DisplayName("generates a token that parses back to the same principal")
    void roundTrip() {
        User user = sampleUser();
        String token = jwtService.generateToken(user);

        SecurityUser principal = jwtService.parse(token);

        assertThat(principal.userId()).isEqualTo(user.id());
        assertThat(principal.merchantId()).isEqualTo(user.merchantId());
        assertThat(principal.email()).isEqualTo(user.email());
        assertThat(principal.role()).isEqualTo(Role.MERCHANT);
    }

    @Test
    @DisplayName("rejects a token signed with a different secret")
    void rejectsForgedToken() {
        String token = new JwtService(new JwtProperties(
                "another-secret-another-secret-0123456789", 60, "europay-hub"))
                .generateToken(sampleUser());

        assertThatThrownBy(() -> jwtService.parse(token)).isInstanceOf(RuntimeException.class);
    }
}
