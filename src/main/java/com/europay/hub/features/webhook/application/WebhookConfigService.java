package com.europay.hub.features.webhook.application;

import com.europay.hub.features.webhook.application.dto.ConfigureWebhookRequest;
import com.europay.hub.features.webhook.application.dto.WebhookEndpointResponse;
import com.europay.hub.features.webhook.domain.WebhookEndpoint;
import com.europay.hub.features.webhook.domain.WebhookEndpointRepository;
import com.europay.hub.shared.event.AuditEvent;
import com.europay.hub.shared.exception.ResourceNotFoundException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Register / view / disable a merchant's webhook endpoint. */
@Service
public class WebhookConfigService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final WebhookEndpointRepository repository;
    private final ApplicationEventPublisher events;

    public WebhookConfigService(WebhookEndpointRepository repository, ApplicationEventPublisher events) {
        this.repository = repository;
        this.events = events;
    }

    @Transactional
    public WebhookEndpointResponse configure(UUID merchantId, ConfigureWebhookRequest request) {
        String secret = (request.secret() == null || request.secret().isBlank())
                ? generateSecret() : request.secret();

        WebhookEndpoint endpoint = repository.findByMerchantId(merchantId)
                .map(existing -> {
                    existing.update(request.url(), secret);
                    return existing;
                })
                .orElseGet(() -> WebhookEndpoint.register(merchantId, request.url(), secret));

        WebhookEndpoint saved = repository.save(endpoint);
        events.publishEvent(new AuditEvent(merchantId, "merchant:" + merchantId, "WEBHOOK_CONFIGURED",
                "WEBHOOK_ENDPOINT", saved.id(), Map.of("url", saved.url())));
        return WebhookEndpointResponse.withSecret(saved);
    }

    @Transactional(readOnly = true)
    public WebhookEndpointResponse get(UUID merchantId) {
        return repository.findByMerchantId(merchantId)
                .map(WebhookEndpointResponse::masked)
                .orElseThrow(() -> new ResourceNotFoundException("Webhook endpoint", merchantId));
    }

    @Transactional
    public void disable(UUID merchantId) {
        repository.findByMerchantId(merchantId).ifPresent(endpoint -> {
            endpoint.disable();
            repository.save(endpoint);
        });
    }

    private static String generateSecret() {
        byte[] bytes = new byte[24];
        RANDOM.nextBytes(bytes);
        return "whsec_" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
