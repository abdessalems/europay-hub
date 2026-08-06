package com.europay.hub.features.webhook.application;

import com.europay.hub.features.webhook.application.dto.ConfigureWebhookRequest;
import com.europay.hub.features.webhook.application.dto.WebhookEndpointResponse;
import com.europay.hub.features.webhook.domain.WebhookEndpoint;
import com.europay.hub.features.webhook.domain.WebhookEndpointRepository;
import com.europay.hub.shared.exception.ResourceNotFoundException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Register / view / disable a merchant's webhook endpoint. */
@Service
public class WebhookConfigService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final WebhookEndpointRepository repository;

    public WebhookConfigService(WebhookEndpointRepository repository) {
        this.repository = repository;
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

        return WebhookEndpointResponse.withSecret(repository.save(endpoint));
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
