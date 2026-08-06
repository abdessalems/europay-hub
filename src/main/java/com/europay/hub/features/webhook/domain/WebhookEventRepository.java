package com.europay.hub.features.webhook.domain;

import com.europay.hub.shared.domain.PageResult;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface WebhookEventRepository {

    WebhookEvent save(WebhookEvent event);

    /** PENDING events whose next attempt is due — ordered oldest first, capped at {@code limit}. */
    List<WebhookEvent> findDispatchable(Instant now, int limit);

    PageResult<WebhookEvent> findByMerchantId(UUID merchantId, int page, int size);
}
