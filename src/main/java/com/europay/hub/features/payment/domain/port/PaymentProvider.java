package com.europay.hub.features.payment.domain.port;

import com.europay.hub.features.payment.domain.Payment;
import com.europay.hub.features.payment.domain.PaymentMethod;

/**
 * Strategy port for a payment method's provider. One implementation per {@link PaymentMethod};
 * the {@code PaymentProviderFactory} selects the right one at runtime. Adding Mastercard/SEPA/…
 * is a new implementation with zero changes elsewhere (Open/Closed).
 */
public interface PaymentProvider {

    PaymentMethod supportedMethod();

    /** Submit the payment to the provider and report the immediate outcome. */
    ProviderResult submit(Payment payment);
}
