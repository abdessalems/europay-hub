package com.europay.hub.features.payment.domain;

import java.util.List;
import java.util.UUID;

public interface RefundRepository {

    Refund save(Refund refund);

    List<Refund> findByPaymentId(UUID paymentId);
}
