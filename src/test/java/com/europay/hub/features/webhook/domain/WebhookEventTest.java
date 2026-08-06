package com.europay.hub.features.webhook.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("WebhookEvent outbox")
class WebhookEventTest {

    private WebhookEvent queued() {
        return WebhookEvent.queue(UUID.randomUUID(), "payment.success", UUID.randomUUID(), "{}");
    }

    @Test
    @DisplayName("starts PENDING with zero attempts")
    void initial() {
        WebhookEvent e = queued();
        assertThat(e.status()).isEqualTo(WebhookStatus.PENDING);
        assertThat(e.attempts()).isZero();
    }

    @Test
    @DisplayName("marks DELIVERED on success")
    void delivered() {
        WebhookEvent e = queued();
        e.markDelivered(200);
        assertThat(e.status()).isEqualTo(WebhookStatus.DELIVERED);
        assertThat(e.attempts()).isEqualTo(1);
        assertThat(e.lastStatusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("retries then FAILS after max attempts, with backoff scheduling")
    void retriesThenFails() {
        WebhookEvent e = queued();

        e.recordFailure(500, "HTTP 500"); // attempt 1
        assertThat(e.status()).isEqualTo(WebhookStatus.PENDING);
        assertThat(e.attempts()).isEqualTo(1);
        assertThat(e.nextAttemptAt()).isAfter(e.createdAt()); // backed off

        e.recordFailure(500, "HTTP 500"); // attempt 2
        assertThat(e.status()).isEqualTo(WebhookStatus.PENDING);

        e.recordFailure(500, "HTTP 500"); // attempt 3 → give up
        assertThat(e.attempts()).isEqualTo(3);
        assertThat(e.status()).isEqualTo(WebhookStatus.FAILED);
        assertThat(e.lastError()).isEqualTo("HTTP 500");
    }
}
