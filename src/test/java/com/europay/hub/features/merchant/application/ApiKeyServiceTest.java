package com.europay.hub.features.merchant.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.europay.hub.features.merchant.application.dto.ApiKeyCreatedResponse;
import com.europay.hub.features.merchant.application.dto.CreateApiKeyRequest;
import com.europay.hub.features.merchant.domain.ApiKey;
import com.europay.hub.features.merchant.domain.ApiKeyRepository;
import com.europay.hub.features.merchant.domain.ApiKeyStatus;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@DisplayName("ApiKeyService")
class ApiKeyServiceTest {

    private final ApiKeyRepository repository = new InMemoryApiKeyRepository();
    private final ApiKeyService service = new ApiKeyService(repository, new BCryptPasswordEncoder(), event -> { });

    @Test
    @DisplayName("creates a key, returns the secret once, and stores only a hash + prefix")
    void createReturnsSecret() {
        UUID merchantId = UUID.randomUUID();

        ApiKeyCreatedResponse created = service.create(merchantId, new CreateApiKeyRequest("Prod", null));

        assertThat(created.secretKey()).startsWith("epk_live_");
        assertThat(created.prefix()).isEqualTo(created.secretKey().substring(0, 16));
        ApiKey stored = repository.findById(created.id()).orElseThrow();
        assertThat(stored.keyHash()).isNotEqualTo(created.secretKey());
        assertThat(stored.status()).isEqualTo(ApiKeyStatus.ACTIVE);
    }

    @Test
    @DisplayName("authenticate resolves a valid raw key to its merchant")
    void authenticateValidKey() {
        UUID merchantId = UUID.randomUUID();
        ApiKeyCreatedResponse created = service.create(merchantId, new CreateApiKeyRequest("Prod", null));

        assertThat(service.authenticate(created.secretKey())).contains(merchantId);
        assertThat(service.authenticate("epk_live_wrongwrongwrong")).isEmpty();
    }

    @Test
    @DisplayName("a revoked key can no longer authenticate")
    void revokedKeyRejected() {
        UUID merchantId = UUID.randomUUID();
        ApiKeyCreatedResponse created = service.create(merchantId, new CreateApiKeyRequest("Prod", null));

        service.revoke(merchantId, created.id());

        assertThat(service.authenticate(created.secretKey())).isEmpty();
    }

    /** Minimal in-memory port implementation for fast, DB-free unit tests. */
    private static final class InMemoryApiKeyRepository implements ApiKeyRepository {
        private final Map<UUID, ApiKey> store = new HashMap<>();

        @Override
        public ApiKey save(ApiKey apiKey) {
            store.put(apiKey.id(), apiKey);
            return apiKey;
        }

        @Override
        public Optional<ApiKey> findById(UUID id) {
            return Optional.ofNullable(store.get(id));
        }

        @Override
        public List<ApiKey> findByMerchantId(UUID merchantId) {
            return store.values().stream().filter(k -> k.merchantId().equals(merchantId)).toList();
        }

        @Override
        public List<ApiKey> findActiveByPrefix(String keyPrefix) {
            return store.values().stream()
                    .filter(k -> k.status() == ApiKeyStatus.ACTIVE && k.keyPrefix().equals(keyPrefix))
                    .toList();
        }
    }
}
