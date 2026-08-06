package com.europay.hub.features.payment.infrastructure.persistence;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, UUID> {

    Page<PaymentEntity> findByMerchantId(UUID merchantId, Pageable pageable);
}
