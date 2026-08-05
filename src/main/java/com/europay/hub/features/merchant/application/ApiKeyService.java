package com.europay.hub.features.merchant.application;

import com.europay.hub.features.merchant.application.dto.ApiKeyCreatedResponse;
import com.europay.hub.features.merchant.application.dto.ApiKeySummaryResponse;
import com.europay.hub.features.merchant.application.dto.CreateApiKeyRequest;
import com.europay.hub.features.merchant.domain.ApiKey;
import com.europay.hub.features.merchant.domain.ApiKeyRepository;
import com.europay.hub.shared.exception.ResourceNotFoundException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manages merchant API keys. Secrets are generated with a CSPRNG, shown once, and stored
 * only as a BCrypt hash plus a short prefix used to narrow verification candidates.
 */
@Service
public class ApiKeyService {

    /** Marks a live secret key, e.g. {@code epk_live_<random>}. */
    static final String LIVE_PREFIX = "epk_live_";
    private static final int PREFIX_LENGTH = 16;
    private static final int SECRET_BYTES = 24;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final ApiKeyRepository apiKeyRepository;
    private final PasswordEncoder passwordEncoder;

    public ApiKeyService(ApiKeyRepository apiKeyRepository, PasswordEncoder passwordEncoder) {
        this.apiKeyRepository = apiKeyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public ApiKeyCreatedResponse create(UUID merchantId, CreateApiKeyRequest request) {
        String secret = generateSecret();
        String prefix = secret.substring(0, PREFIX_LENGTH);
        String hash = passwordEncoder.encode(secret);

        ApiKey saved = apiKeyRepository.save(
                ApiKey.issue(merchantId, request.name(), prefix, hash, request.expiresAt()));
        return ApiKeyCreatedResponse.from(saved, secret);
    }

    @Transactional(readOnly = true)
    public List<ApiKeySummaryResponse> list(UUID merchantId) {
        return apiKeyRepository.findByMerchantId(merchantId).stream()
                .map(ApiKeySummaryResponse::from)
                .toList();
    }

    @Transactional
    public void revoke(UUID merchantId, UUID keyId) {
        ApiKey key = apiKeyRepository.findById(keyId)
                .filter(k -> k.merchantId().equals(merchantId)) // never reveal another merchant's key
                .orElseThrow(() -> new ResourceNotFoundException("API key", keyId));
        key.revoke();
        apiKeyRepository.save(key);
    }

    /**
     * Resolve a raw API key to its owning merchant, if valid. Used by the API-key auth filter
     * in later phases (payment endpoints). Updates {@code lastUsedAt} on success.
     */
    @Transactional
    public Optional<UUID> authenticate(String rawKey) {
        if (rawKey == null || !rawKey.startsWith(LIVE_PREFIX) || rawKey.length() < PREFIX_LENGTH) {
            return Optional.empty();
        }
        String prefix = rawKey.substring(0, PREFIX_LENGTH);
        Instant now = Instant.now();
        for (ApiKey candidate : apiKeyRepository.findActiveByPrefix(prefix)) {
            if (candidate.isUsable(now) && passwordEncoder.matches(rawKey, candidate.keyHash())) {
                candidate.markUsed(now);
                apiKeyRepository.save(candidate);
                return Optional.of(candidate.merchantId());
            }
        }
        return Optional.empty();
    }

    private static String generateSecret() {
        byte[] bytes = new byte[SECRET_BYTES];
        RANDOM.nextBytes(bytes);
        return LIVE_PREFIX + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
