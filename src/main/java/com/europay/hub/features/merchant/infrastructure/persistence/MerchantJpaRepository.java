package com.europay.hub.features.merchant.infrastructure.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantJpaRepository extends JpaRepository<MerchantEntity, UUID> {

    boolean existsByEmail(String email);
}
