package com.europay.hub.features.payment.infrastructure.persistence;

import com.europay.hub.features.payment.domain.PaymentStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, UUID> {

    Page<PaymentEntity> findByMerchantId(UUID merchantId, Pageable pageable);

    List<PaymentEntity> findByStatusAndCreatedAtBefore(PaymentStatus status, Instant cutoff);
}
