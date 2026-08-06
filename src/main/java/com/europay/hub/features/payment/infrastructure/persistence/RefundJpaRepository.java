package com.europay.hub.features.payment.infrastructure.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundJpaRepository extends JpaRepository<RefundEntity, UUID> {

    List<RefundEntity> findByPaymentId(UUID paymentId);
}
