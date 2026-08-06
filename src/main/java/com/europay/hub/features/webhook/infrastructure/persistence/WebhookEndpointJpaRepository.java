package com.europay.hub.features.webhook.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookEndpointJpaRepository extends JpaRepository<WebhookEndpointEntity, UUID> {

    Optional<WebhookEndpointEntity> findByMerchantId(UUID merchantId);
}
