package com.europay.hub.features.webhook.application.port;

/** Port for actually delivering a signed webhook over HTTP. Implemented in infrastructure. */
public interface WebhookSender {

    DeliveryResult send(String url, String secret, String payload);

    /**
     * @param statusCode HTTP status if a response was received, else {@code null}
     * @param success    true when the endpoint returned 2xx
     * @param error      short error message when not successful
     */
    record DeliveryResult(Integer statusCode, boolean success, String error) {
    }
}
