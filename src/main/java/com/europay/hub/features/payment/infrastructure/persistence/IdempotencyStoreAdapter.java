package com.europay.hub.features.payment.infrastructure.persistence;

import com.europay.hub.features.payment.application.port.IdempotencyRecord;
import com.europay.hub.features.payment.application.port.IdempotencyStore;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class IdempotencyStoreAdapter implements IdempotencyStore {

    private final IdempotencyKeyJpaRepository jpa;

    public IdempotencyStoreAdapter(IdempotencyKeyJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<IdempotencyRecord> find(UUID merchantId, String key) {
        return jpa.findByMerchantIdAndIdempotencyKey(merchantId, key).map(e ->
                new IdempotencyRecord(e.getMerchantId(), e.getIdempotencyKey(), e.getRequestHash(), e.getPaymentId()));
    }

    @Override
    public void save(IdempotencyRecord record) {
        IdempotencyKeyEntity e = new IdempotencyKeyEntity();
        e.setId(UUID.randomUUID());
        e.setMerchantId(record.merchantId());
        e.setIdempotencyKey(record.key());
        e.setRequestHash(record.requestHash());
        e.setPaymentId(record.paymentId());
        e.setCreatedAt(Instant.now());
        jpa.save(e);
    }
}
