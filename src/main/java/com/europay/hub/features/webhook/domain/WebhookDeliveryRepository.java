package com.europay.hub.features.webhook.domain;

import java.util.List;
import java.util.UUID;

public interface WebhookDeliveryRepository {

    WebhookDelivery save(WebhookDelivery delivery);

    List<WebhookDelivery> findByWebhookEventId(UUID webhookEventId);
}
