package com.europay.hub.features.webhook.application;

import com.europay.hub.features.payment.domain.event.PaymentDomainEvent;
import com.europay.hub.features.webhook.domain.WebhookEndpointRepository;
import com.europay.hub.features.webhook.domain.WebhookEvent;
import com.europay.hub.features.webhook.domain.WebhookEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Transactional outbox: on a payment event, if the merchant has an active webhook endpoint,
 * persist a {@link WebhookEvent} to deliver. This runs synchronously inside the payment's
 * transaction, so the outbox row commits atomically with the payment change.
 */
@Component
public class PaymentEventOutboxListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventOutboxListener.class);

    private final WebhookEndpointRepository endpointRepository;
    private final WebhookEventRepository eventRepository;
    private final ObjectMapper objectMapper;

    public PaymentEventOutboxListener(WebhookEndpointRepository endpointRepository,
                                      WebhookEventRepository eventRepository,
                                      ObjectMapper objectMapper) {
        this.endpointRepository = endpointRepository;
        this.eventRepository = eventRepository;
        this.objectMapper = objectMapper;
    }

    @EventListener
    public void on(PaymentDomainEvent event) {
        boolean hasActiveEndpoint = endpointRepository.findByMerchantId(event.merchantId())
                .map(e -> e.isActive())
                .orElse(false);
        if (!hasActiveEndpoint) {
            return; // nothing to deliver to
        }
        eventRepository.save(WebhookEvent.queue(
                event.merchantId(), event.eventType(), event.paymentId(), buildPayload(event)));
    }

    private String buildPayload(PaymentDomainEvent event) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("paymentId", event.paymentId());
        data.put("orderId", event.orderId());
        data.put("merchantId", event.merchantId());
        data.put("amount", new BigDecimal(event.amountMinor()).movePointLeft(2));
        data.put("currency", event.currency());
        data.put("status", event.status());
        data.put("method", event.method());
        data.put("providerReference", event.providerReference());

        Map<String, Object> root = new LinkedHashMap<>();
        root.put("type", event.eventType());
        root.put("createdAt", event.occurredAt().toString());
        root.put("data", data);
        try {
            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            log.warn("Failed to serialize webhook payload", e);
            return "{\"type\":\"" + event.eventType() + "\"}";
        }
    }
}
