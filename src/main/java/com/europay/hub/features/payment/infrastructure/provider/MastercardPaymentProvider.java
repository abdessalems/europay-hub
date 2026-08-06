package com.europay.hub.features.payment.infrastructure.provider;

import com.europay.hub.features.payment.domain.Payment;
import com.europay.hub.features.payment.domain.PaymentMethod;
import com.europay.hub.features.payment.domain.port.PaymentProvider;
import com.europay.hub.features.payment.domain.port.ProviderResult;
import java.util.UUID;
import org.springframework.stereotype.Component;

/** Mock Mastercard provider — card flow, authorizes immediately. */
@Component
public class MastercardPaymentProvider implements PaymentProvider {

    @Override
    public PaymentMethod supportedMethod() {
        return PaymentMethod.MASTERCARD;
    }

    @Override
    public ProviderResult submit(Payment payment) {
        return ProviderResult.authorized("MC-" + ref());
    }

    private static String ref() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
}
