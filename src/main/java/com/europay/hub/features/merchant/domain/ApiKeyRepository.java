package com.europay.hub.features.merchant.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Domain port for {@link ApiKey} persistence. */
public interface ApiKeyRepository {

    ApiKey save(ApiKey apiKey);

    Optional<ApiKey> findById(UUID id);

    List<ApiKey> findByMerchantId(UUID merchantId);

    /** Active keys sharing a prefix — candidates for hash verification. */
    List<ApiKey> findActiveByPrefix(String keyPrefix);
}
