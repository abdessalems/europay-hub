package com.europay.hub.features.webhook.infrastructure.persistence;

import com.europay.hub.features.webhook.domain.WebhookDelivery;
import com.europay.hub.features.webhook.domain.WebhookDeliveryRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class WebhookDeliveryRepositoryAdapter implements WebhookDeliveryRepository {

    private final WebhookDeliveryJpaRepository jpa;

    public WebhookDeliveryRepositoryAdapter(WebhookDeliveryJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public WebhookDelivery save(WebhookDelivery delivery) {
        return toDomain(jpa.save(toEntity(delivery)));
    }

    @Override
    public List<WebhookDelivery> findByWebhookEventId(UUID webhookEventId) {
        return jpa.findByWebhookEventIdOrderByAttemptAsc(webhookEventId).stream()
                .map(WebhookDeliveryRepositoryAdapter::toDomain)
                .toList();
    }

    private static WebhookDeliveryEntity toEntity(WebhookDelivery d) {
        WebhookDeliveryEntity entity = new WebhookDeliveryEntity();
        entity.setId(d.id());
        entity.setWebhookEventId(d.webhookEventId());
        entity.setAttempt(d.attempt());
        entity.setStatusCode(d.statusCode());
        entity.setSuccess(d.success());
        entity.setError(d.error());
        entity.setCreatedAt(d.createdAt());
        return entity;
    }

    private static WebhookDelivery toDomain(WebhookDeliveryEntity e) {
        return new WebhookDelivery(e.getId(), e.getWebhookEventId(), e.getAttempt(), e.getStatusCode(),
                e.isSuccess(), e.getError(), e.getCreatedAt());
    }
}
