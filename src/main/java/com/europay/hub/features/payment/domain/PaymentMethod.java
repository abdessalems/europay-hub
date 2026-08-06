package com.europay.hub.features.payment.domain;

/**
 * Supported payment methods (mock providers). New methods (Mastercard, SEPA Instant,
 * PayPal, Apple Pay) plug in by adding an enum value and a matching provider.
 */
public enum PaymentMethod {
    WERO,
    BANCONTACT,
    VISA
}
