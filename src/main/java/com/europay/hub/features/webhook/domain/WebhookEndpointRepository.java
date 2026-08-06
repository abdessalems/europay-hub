package com.europay.hub.features.webhook.domain;

import java.util.Optional;
import java.util.UUID;

public interface WebhookEndpointRepository {

    WebhookEndpoint save(WebhookEndpoint endpoint);

    Optional<WebhookEndpoint> findByMerchantId(UUID merchantId);
}
