package com.europay.hub.features.webhook.domain;

/** Wire names for webhook event types (as sent in the payload's {@code type} field). */
public final class WebhookEventType {

    public static final String PAYMENT_CREATED = "payment.created";
    public static final String PAYMENT_PENDING = "payment.pending";
    public static final String PAYMENT_AUTHORIZED = "payment.authorized";
    public static final String PAYMENT_SUCCESS = "payment.success";
    public static final String PAYMENT_FAILED = "payment.failed";
    public static final String PAYMENT_REFUNDED = "payment.refunded";

    private WebhookEventType() {
    }
}
