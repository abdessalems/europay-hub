package com.europay.hub.features.webhook.infrastructure.persistence;

import com.europay.hub.features.webhook.domain.WebhookEndpoint;
import com.europay.hub.features.webhook.domain.WebhookEndpointRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class WebhookEndpointRepositoryAdapter implements WebhookEndpointRepository {

    private final WebhookEndpointJpaRepository jpa;

    public WebhookEndpointRepositoryAdapter(WebhookEndpointJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public WebhookEndpoint save(WebhookEndpoint endpoint) {
        return toDomain(jpa.save(toEntity(endpoint)));
    }

    @Override
    public Optional<WebhookEndpoint> findByMerchantId(UUID merchantId) {
        return jpa.findByMerchantId(merchantId).map(WebhookEndpointRepositoryAdapter::toDomain);
    }

    private static WebhookEndpointEntity toEntity(WebhookEndpoint e) {
        WebhookEndpointEntity entity = new WebhookEndpointEntity();
        entity.setId(e.id());
        entity.setMerchantId(e.merchantId());
        entity.setUrl(e.url());
        entity.setSecret(e.secret());
        entity.setActive(e.isActive());
        entity.setCreatedAt(e.createdAt());
        entity.setUpdatedAt(Instant.now());
        return entity;
    }

    private static WebhookEndpoint toDomain(WebhookEndpointEntity e) {
        return new WebhookEndpoint(e.getId(), e.getMerchantId(), e.getUrl(), e.getSecret(), e.isActive(), e.getCreatedAt());
    }
}
