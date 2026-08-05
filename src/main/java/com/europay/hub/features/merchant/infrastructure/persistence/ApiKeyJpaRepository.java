package com.europay.hub.features.merchant.infrastructure.persistence;

import com.europay.hub.features.merchant.domain.ApiKeyStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiKeyJpaRepository extends JpaRepository<ApiKeyEntity, UUID> {

    List<ApiKeyEntity> findByMerchantIdOrderByCreatedAtDesc(UUID merchantId);

    List<ApiKeyEntity> findByKeyPrefixAndStatus(String keyPrefix, ApiKeyStatus status);
}
