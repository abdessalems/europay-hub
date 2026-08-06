package com.europay.hub.features.payment.infrastructure.provider;

import com.europay.hub.features.payment.domain.Payment;
import com.europay.hub.features.payment.domain.PaymentMethod;
import com.europay.hub.features.payment.domain.port.PaymentProvider;
import com.europay.hub.features.payment.domain.port.ProviderResult;
import java.util.UUID;
import org.springframework.stereotype.Component;

/** Mock Visa provider — card flow, authorizes immediately (funds reserved). */
@Component
public class VisaPaymentProvider implements PaymentProvider {

    @Override
    public PaymentMethod supportedMethod() {
        return PaymentMethod.VISA;
    }

    @Override
    public ProviderResult submit(Payment payment) {
        return ProviderResult.authorized("VISA-" + shortRef());
    }

    private static String shortRef() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
}
