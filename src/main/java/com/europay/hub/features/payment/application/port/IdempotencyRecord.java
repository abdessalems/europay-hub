package com.europay.hub.features.payment.application.port;

import java.util.UUID;

/**
 * A stored idempotency outcome: which payment a given (merchant, key) produced, plus the hash
 * of the original request so a reused key with a different body can be rejected.
 */
public record IdempotencyRecord(UUID merchantId, String key, String requestHash, UUID paymentId) {
}
