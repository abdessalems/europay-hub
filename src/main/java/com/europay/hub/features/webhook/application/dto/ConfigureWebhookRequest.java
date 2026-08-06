package com.europay.hub.features.webhook.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Configure the merchant's webhook. {@code secret} is optional — one is generated if omitted.
 */
public record ConfigureWebhookRequest(

        @NotBlank
        @Size(max = 500)
        @Pattern(regexp = "^https?://.+", message = "must be an http(s) URL")
        String url,

        @Size(max = 100)
        String secret) {
}
