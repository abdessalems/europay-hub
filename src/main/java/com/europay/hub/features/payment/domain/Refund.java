package com.europay.hub.features.payment.domain;

import com.europay.hub.shared.domain.AggregateRoot;
import com.europay.hub.shared.domain.Money;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** A return of funds for a previously successful payment. Mock refunds settle immediately. */
public class Refund extends AggregateRoot<UUID> {

    private final UUID id;
    private final UUID paymentId;
    private final UUID merchantId;
    private final Money amount;
    private final String reason;
    private final Instant createdAt;

    public Refund(UUID id, UUID paymentId, UUID merchantId, Money amount, String reason, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.paymentId = Objects.requireNonNull(paymentId, "paymentId");
        this.merchantId = Objects.requireNonNull(merchantId, "merchantId");
        this.amount = Objects.requireNonNull(amount, "amount");
        this.reason = reason;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public static Refund create(UUID paymentId, UUID merchantId, Money amount, String reason) {
        return new Refund(UUID.randomUUID(), paymentId, merchantId, amount, reason, Instant.now());
    }

    @Override
    public UUID id() {
        return id;
    }

    public UUID paymentId() {
        return paymentId;
    }

    public UUID merchantId() {
        return merchantId;
    }

    public Money amount() {
        return amount;
    }

    public String reason() {
        return reason;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
