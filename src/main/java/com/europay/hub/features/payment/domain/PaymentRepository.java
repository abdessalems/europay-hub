package com.europay.hub.features.payment.domain;

import com.europay.hub.shared.domain.PageResult;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findById(UUID id);

    PageResult<Payment> findByMerchantId(UUID merchantId, int page, int size);
}
