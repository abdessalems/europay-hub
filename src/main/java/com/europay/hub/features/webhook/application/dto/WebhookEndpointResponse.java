package com.europay.hub.features.webhook.application.dto;

import com.europay.hub.features.webhook.domain.WebhookEndpoint;
import java.time.Instant;

/** {@code secret} is the real value only right after configuring; otherwise it is masked. */
public record WebhookEndpointResponse(
        String url,
        boolean active,
        String secret,
        Instant createdAt) {

    public static WebhookEndpointResponse withSecret(WebhookEndpoint e) {
        return new WebhookEndpointResponse(e.url(), e.isActive(), e.secret(), e.createdAt());
    }

    public static WebhookEndpointResponse masked(WebhookEndpoint e) {
        String s = e.secret();
        String masked = s.length() <= 10 ? "whsec_••••" : s.substring(0, 10) + "••••";
        return new WebhookEndpointResponse(e.url(), e.isActive(), masked, e.createdAt());
    }
}
