package com.europay.hub.features.payment.infrastructure.provider;

import com.europay.hub.features.payment.domain.PaymentMethod;
import com.europay.hub.features.payment.domain.port.PaymentProvider;
import com.europay.hub.features.payment.domain.port.PaymentProviderRegistry;
import com.europay.hub.shared.exception.BusinessRuleViolationException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Wires every {@link PaymentProvider} bean into a method → provider map (Factory + Strategy).
 * Spring injects all providers, so a new method is registered simply by adding a bean.
 */
@Component
public class PaymentProviderFactory implements PaymentProviderRegistry {

    private final Map<PaymentMethod, PaymentProvider> providers = new EnumMap<>(PaymentMethod.class);

    public PaymentProviderFactory(List<PaymentProvider> providerBeans) {
        for (PaymentProvider provider : providerBeans) {
            providers.put(provider.supportedMethod(), provider);
        }
    }

    @Override
    public PaymentProvider forMethod(PaymentMethod method) {
        PaymentProvider provider = providers.get(method);
        if (provider == null) {
            throw new BusinessRuleViolationException(
                    "UNSUPPORTED_PAYMENT_METHOD", "No provider configured for " + method);
        }
        return provider;
    }
}
