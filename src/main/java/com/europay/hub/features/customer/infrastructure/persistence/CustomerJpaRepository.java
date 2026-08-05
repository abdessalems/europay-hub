package com.europay.hub.features.customer.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, UUID> {

    Optional<CustomerEntity> findByMerchantIdAndEmail(UUID merchantId, String email);

    Page<CustomerEntity> findByMerchantId(UUID merchantId, Pageable pageable);
}
