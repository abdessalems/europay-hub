package com.europay.hub.features.payment.infrastructure.provider;

import com.europay.hub.features.payment.domain.Payment;
import com.europay.hub.features.payment.domain.PaymentMethod;
import com.europay.hub.features.payment.domain.port.PaymentProvider;
import com.europay.hub.features.payment.domain.port.ProviderResult;
import java.util.UUID;
import org.springframework.stereotype.Component;

/** Mock Apple Pay provider — tokenized card, authorizes immediately. */
@Component
public class ApplePayPaymentProvider implements PaymentProvider {

    @Override
    public PaymentMethod supportedMethod() {
        return PaymentMethod.APPLE_PAY;
    }

    @Override
    public ProviderResult submit(Payment payment) {
        return ProviderResult.authorized("APAY-" + ref());
    }

    private static String ref() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
}
