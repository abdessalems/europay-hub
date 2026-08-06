package com.europay.hub.features.payment.domain.port;

/**
 * What a provider returns after a submit.
 *
 * @param providerReference the provider's own reference for the transaction
 * @param outcome           the immediate outcome
 * @param declineReason     populated when {@code outcome == DECLINED}
 */
public record ProviderResult(String providerReference, ProviderOutcome outcome, String declineReason) {

    public static ProviderResult pending(String providerReference) {
        return new ProviderResult(providerReference, ProviderOutcome.PENDING, null);
    }

    public static ProviderResult authorized(String providerReference) {
        return new ProviderResult(providerReference, ProviderOutcome.AUTHORIZED, null);
    }

    public static ProviderResult declined(String providerReference, String reason) {
        return new ProviderResult(providerReference, ProviderOutcome.DECLINED, reason);
    }
}
