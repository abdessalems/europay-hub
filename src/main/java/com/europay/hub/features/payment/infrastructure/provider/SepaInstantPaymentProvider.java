package com.europay.hub.features.payment.infrastructure.provider;

import com.europay.hub.features.payment.domain.Payment;
import com.europay.hub.features.payment.domain.PaymentMethod;
import com.europay.hub.features.payment.domain.port.PaymentProvider;
import com.europay.hub.features.payment.domain.port.ProviderResult;
import java.util.UUID;
import org.springframework.stereotype.Component;

/** Mock SEPA Instant provider — account-to-account, returns PENDING awaiting confirmation. */
@Component
public class SepaInstantPaymentProvider implements PaymentProvider {

    @Override
    public PaymentMethod supportedMethod() {
        return PaymentMethod.SEPA_INSTANT;
    }

    @Override
    public ProviderResult submit(Payment payment) {
        return ProviderResult.pending("SEPA-" + ref());
    }

    private static String ref() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
}
