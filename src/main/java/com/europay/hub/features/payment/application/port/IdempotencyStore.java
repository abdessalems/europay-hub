package com.europay.hub.features.payment.application.port;

import java.util.Optional;
import java.util.UUID;

/** Port for persisting idempotency outcomes; implemented by an infrastructure adapter. */
public interface IdempotencyStore {

    Optional<IdempotencyRecord> find(UUID merchantId, String key);

    void save(IdempotencyRecord record);
}
