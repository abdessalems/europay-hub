package com.europay.hub.features.merchant.domain;

import java.util.Optional;
import java.util.UUID;

/** Domain port for {@link Merchant} persistence. */
public interface MerchantRepository {

    Merchant save(Merchant merchant);

    Optional<Merchant> findById(UUID id);

    boolean existsByEmail(String email);
}
