package com.europay.hub.features.payment.domain.port;

import com.europay.hub.features.payment.domain.PaymentMethod;

/** Resolves the {@link PaymentProvider} for a method. Implemented in the infrastructure layer. */
public interface PaymentProviderRegistry {

    PaymentProvider forMethod(PaymentMethod method);
}
