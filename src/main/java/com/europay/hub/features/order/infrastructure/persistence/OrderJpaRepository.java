package com.europay.hub.features.order.infrastructure.persistence;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {

    Page<OrderEntity> findByMerchantId(UUID merchantId, Pageable pageable);

    Page<OrderEntity> findByMerchantIdAndCustomerId(UUID merchantId, UUID customerId, Pageable pageable);

    boolean existsByMerchantIdAndReference(UUID merchantId, String reference);
}
