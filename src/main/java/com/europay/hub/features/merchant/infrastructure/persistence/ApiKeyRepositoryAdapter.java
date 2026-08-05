package com.europay.hub.features.merchant.infrastructure.persistence;

import com.europay.hub.features.merchant.domain.ApiKey;
import com.europay.hub.features.merchant.domain.ApiKeyRepository;
import com.europay.hub.features.merchant.domain.ApiKeyStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class ApiKeyRepositoryAdapter implements ApiKeyRepository {

    private final ApiKeyJpaRepository jpa;

    public ApiKeyRepositoryAdapter(ApiKeyJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public ApiKey save(ApiKey apiKey) {
        return toDomain(jpa.save(toEntity(apiKey)));
    }

    @Override
    public Optional<ApiKey> findById(UUID id) {
        return jpa.findById(id).map(ApiKeyRepositoryAdapter::toDomain);
    }

    @Override
    public List<ApiKey> findByMerchantId(UUID merchantId) {
        return jpa.findByMerchantIdOrderByCreatedAtDesc(merchantId).stream()
                .map(ApiKeyRepositoryAdapter::toDomain)
                .toList();
    }

    @Override
    public List<ApiKey> findActiveByPrefix(String keyPrefix) {
        return jpa.findByKeyPrefixAndStatus(keyPrefix, ApiKeyStatus.ACTIVE).stream()
                .map(ApiKeyRepositoryAdapter::toDomain)
                .toList();
    }

    private static ApiKeyEntity toEntity(ApiKey k) {
        ApiKeyEntity e = new ApiKeyEntity();
        e.setId(k.id());
        e.setMerchantId(k.merchantId());
        e.setName(k.name());
        e.setKeyPrefix(k.keyPrefix());
        e.setKeyHash(k.keyHash());
        e.setStatus(k.status());
        e.setCreatedAt(k.createdAt());
        e.setLastUsedAt(k.lastUsedAt());
        e.setExpiresAt(k.expiresAt());
        return e;
    }

    private static ApiKey toDomain(ApiKeyEntity e) {
        return new ApiKey(e.getId(), e.getMerchantId(), e.getName(), e.getKeyPrefix(), e.getKeyHash(),
                e.getStatus(), e.getCreatedAt(), e.getLastUsedAt(), e.getExpiresAt());
    }
}
