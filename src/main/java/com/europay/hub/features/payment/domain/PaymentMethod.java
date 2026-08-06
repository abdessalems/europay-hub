package com.europay.hub.features.payment.domain;

/**
 * Supported payment methods (mock providers). Adding a method is just a new enum value plus a
 * matching {@code PaymentProvider} bean — the factory registers it automatically (Open/Closed).
 */
public enum PaymentMethod {
    WERO,
    BANCONTACT,
    VISA,
    MASTERCARD,
    SEPA_INSTANT,
    PAYPAL,
    APPLE_PAY
}
