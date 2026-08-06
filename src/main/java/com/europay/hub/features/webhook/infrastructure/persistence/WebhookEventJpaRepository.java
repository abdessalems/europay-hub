package com.europay.hub.features.webhook.infrastructure.persistence;

import com.europay.hub.features.webhook.domain.WebhookStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookEventJpaRepository extends JpaRepository<WebhookEventEntity, UUID> {

    List<WebhookEventEntity> findByStatusAndNextAttemptAtLessThanEqualOrderByCreatedAtAsc(
            WebhookStatus status, Instant now, Pageable pageable);

    Page<WebhookEventEntity> findByMerchantId(UUID merchantId, Pageable pageable);
}
