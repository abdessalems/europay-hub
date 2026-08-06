package com.europay.hub.features.webhook.infrastructure.persistence;

import com.europay.hub.features.webhook.domain.WebhookEvent;
import com.europay.hub.features.webhook.domain.WebhookEventRepository;
import com.europay.hub.features.webhook.domain.WebhookStatus;
import com.europay.hub.shared.domain.PageResult;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class WebhookEventRepositoryAdapter implements WebhookEventRepository {

    private final WebhookEventJpaRepository jpa;

    public WebhookEventRepositoryAdapter(WebhookEventJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public WebhookEvent save(WebhookEvent event) {
        return toDomain(jpa.save(toEntity(event)));
    }

    @Override
    public List<WebhookEvent> findDispatchable(Instant now, int limit) {
        return jpa.findByStatusAndNextAttemptAtLessThanEqualOrderByCreatedAtAsc(
                        WebhookStatus.PENDING, now, PageRequest.of(0, limit)).stream()
                .map(WebhookEventRepositoryAdapter::toDomain)
                .toList();
    }

    @Override
    public PageResult<WebhookEvent> findByMerchantId(UUID merchantId, int page, int size) {
        Page<WebhookEventEntity> result = jpa.findByMerchantId(merchantId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return new PageResult<>(
                result.getContent().stream().map(WebhookEventRepositoryAdapter::toDomain).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    private static WebhookEventEntity toEntity(WebhookEvent e) {
        WebhookEventEntity entity = new WebhookEventEntity();
        entity.setId(e.id());
        entity.setMerchantId(e.merchantId());
        entity.setEventType(e.eventType());
        entity.setPaymentId(e.paymentId());
        entity.setPayload(e.payload());
        entity.setStatus(e.status());
        entity.setAttempts(e.attempts());
        entity.setMaxAttempts(e.maxAttempts());
        entity.setNextAttemptAt(e.nextAttemptAt());
        entity.setLastStatusCode(e.lastStatusCode());
        entity.setLastError(e.lastError());
        entity.setCreatedAt(e.createdAt());
        return entity;
    }

    private static WebhookEvent toDomain(WebhookEventEntity e) {
        return new WebhookEvent(e.getId(), e.getMerchantId(), e.getEventType(), e.getPaymentId(), e.getPayload(),
                e.getStatus(), e.getAttempts(), e.getMaxAttempts(), e.getNextAttemptAt(),
                e.getLastStatusCode(), e.getLastError(), e.getCreatedAt());
    }
}
