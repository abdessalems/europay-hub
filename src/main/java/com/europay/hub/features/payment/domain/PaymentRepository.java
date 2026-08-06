package com.europay.hub.features.payment.domain;

import com.europay.hub.shared.domain.PageResult;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findById(UUID id);

    PageResult<Payment> findByMerchantId(UUID merchantId, int page, int size);

    /** PENDING payments created before {@code cutoff} — candidates for expiry. */
    List<Payment> findExpirable(Instant cutoff);
}
