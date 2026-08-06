package com.europay.hub.features.payment.domain.port;

/** The immediate result of submitting a payment to a provider. */
public enum ProviderOutcome {
    /** Accepted; awaiting customer approval / async confirmation (typical for Wero/Bancontact). */
    PENDING,
    /** Funds reserved immediately (typical card flow). */
    AUTHORIZED,
    /** Rejected by the provider. */
    DECLINED
}
