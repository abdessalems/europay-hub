package com.europay.hub.features.webhook.infrastructure.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookDeliveryJpaRepository extends JpaRepository<WebhookDeliveryEntity, UUID> {

    List<WebhookDeliveryEntity> findByWebhookEventIdOrderByAttemptAsc(UUID webhookEventId);
}
