package com.europay.hub.features.order.domain;

import com.europay.hub.shared.domain.AggregateRoot;
import com.europay.hub.shared.domain.Money;
import com.europay.hub.shared.exception.BusinessRuleViolationException;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A merchant's request for a customer to pay a specific amount. Guards its own lifecycle:
 * only a {@code CREATED} order may be cancelled or marked paid.
 */
public class Order extends AggregateRoot<UUID> {

    private final UUID id;
    private final UUID merchantId;
    private final UUID customerId;
    private final String reference;
    private final Money amount;
    private OrderStatus status;
    private final Instant createdAt;

    public Order(UUID id, UUID merchantId, UUID customerId, String reference,
                 Money amount, OrderStatus status, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.merchantId = Objects.requireNonNull(merchantId, "merchantId");
        this.customerId = Objects.requireNonNull(customerId, "customerId");
        this.reference = Objects.requireNonNull(reference, "reference");
        this.amount = Objects.requireNonNull(amount, "amount");
        this.status = Objects.requireNonNull(status, "status");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public static Order create(UUID merchantId, UUID customerId, String reference, Money amount) {
        if (!amount.isPositive()) {
            throw new BusinessRuleViolationException("INVALID_AMOUNT", "Order amount must be positive");
        }
        return new Order(UUID.randomUUID(), merchantId, customerId, reference,
                amount, OrderStatus.CREATED, Instant.now());
    }

    public void cancel() {
        if (status != OrderStatus.CREATED) {
            throw new BusinessRuleViolationException(
                    "ORDER_NOT_CANCELLABLE", "Only a CREATED order can be cancelled (was " + status + ")");
        }
        this.status = OrderStatus.CANCELLED;
    }

    /** Marked when the associated payment succeeds (Phase 4). */
    public void markPaid() {
        if (status != OrderStatus.CREATED) {
            throw new BusinessRuleViolationException(
                    "ORDER_NOT_PAYABLE", "Only a CREATED order can be marked paid (was " + status + ")");
        }
        this.status = OrderStatus.PAID;
    }

    @Override
    public UUID id() {
        return id;
    }

    public UUID merchantId() {
        return merchantId;
    }

    public UUID customerId() {
        return customerId;
    }

    public String reference() {
        return reference;
    }

    public Money amount() {
        return amount;
    }

    public OrderStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
