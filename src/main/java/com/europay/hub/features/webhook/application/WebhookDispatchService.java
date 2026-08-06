package com.europay.hub.features.webhook.application;

import com.europay.hub.features.webhook.application.port.WebhookSender;
import com.europay.hub.features.webhook.domain.WebhookDelivery;
import com.europay.hub.features.webhook.domain.WebhookDeliveryRepository;
import com.europay.hub.features.webhook.domain.WebhookEndpoint;
import com.europay.hub.features.webhook.domain.WebhookEndpointRepository;
import com.europay.hub.features.webhook.domain.WebhookEvent;
import com.europay.hub.features.webhook.domain.WebhookEventRepository;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Delivers due outbox events: signs and POSTs each to the merchant's endpoint, logs the attempt,
 * and either marks it DELIVERED or schedules a retry (up to max attempts) with backoff.
 */
@Service
public class WebhookDispatchService {

    private static final Logger log = LoggerFactory.getLogger(WebhookDispatchService.class);
    private static final int BATCH = 50;

    private final WebhookEventRepository eventRepository;
    private final WebhookEndpointRepository endpointRepository;
    private final WebhookDeliveryRepository deliveryRepository;
    private final WebhookSender sender;

    public WebhookDispatchService(WebhookEventRepository eventRepository,
                                  WebhookEndpointRepository endpointRepository,
                                  WebhookDeliveryRepository deliveryRepository,
                                  WebhookSender sender) {
        this.eventRepository = eventRepository;
        this.endpointRepository = endpointRepository;
        this.deliveryRepository = deliveryRepository;
        this.sender = sender;
    }

    @Transactional
    public int dispatchDue() {
        var due = eventRepository.findDispatchable(Instant.now(), BATCH);
        for (WebhookEvent event : due) {
            deliver(event);
        }
        return due.size();
    }

    private void deliver(WebhookEvent event) {
        Optional<WebhookEndpoint> endpoint = endpointRepository.findByMerchantId(event.merchantId());
        if (endpoint.isEmpty() || !endpoint.get().isActive()) {
            event.recordFailure(null, "No active webhook endpoint");
            deliveryRepository.save(WebhookDelivery.record(event.id(), event.attempts(), null, false, "No active endpoint"));
            eventRepository.save(event);
            return;
        }

        WebhookEndpoint ep = endpoint.get();
        WebhookSender.DeliveryResult result = sender.send(ep.url(), ep.secret(), event.payload());

        if (result.success()) {
            event.markDelivered(result.statusCode());
        } else {
            event.recordFailure(result.statusCode(), result.error());
            log.debug("Webhook {} attempt {} failed: {}", event.id(), event.attempts(), result.error());
        }
        deliveryRepository.save(WebhookDelivery.record(
                event.id(), event.attempts(), result.statusCode(), result.success(), result.error()));
        eventRepository.save(event);
    }
}
