package com.europay.hub.features.payment.domain;

import com.europay.hub.shared.domain.AggregateRoot;
import com.europay.hub.shared.domain.Money;
import com.europay.hub.shared.exception.BusinessRuleViolationException;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * The core aggregate. Every state change goes through {@link #transitionTo} which consults
 * {@link PaymentStatus} for legality, so the aggregate can never reach an invalid state.
 */
public class Payment extends AggregateRoot<UUID> {

    private final UUID id;
    private final UUID merchantId;
    private final UUID orderId;
    private final PaymentMethod method;
    private final Money amount;
    private PaymentStatus status;
    private String providerReference;
    private String failureReason;
    private final Instant createdAt;

    public Payment(UUID id, UUID merchantId, UUID orderId, PaymentMethod method, Money amount,
                   PaymentStatus status, String providerReference, String failureReason, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.merchantId = Objects.requireNonNull(merchantId, "merchantId");
        this.orderId = Objects.requireNonNull(orderId, "orderId");
        this.method = Objects.requireNonNull(method, "method");
        this.amount = Objects.requireNonNull(amount, "amount");
        this.status = Objects.requireNonNull(status, "status");
        this.providerReference = providerReference;
        this.failureReason = failureReason;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public static Payment create(UUID merchantId, UUID orderId, PaymentMethod method, Money amount) {
        if (!amount.isPositive()) {
            throw new BusinessRuleViolationException("INVALID_AMOUNT", "Payment amount must be positive");
        }
        return new Payment(UUID.randomUUID(), merchantId, orderId, method, amount,
                PaymentStatus.CREATED, null, null, Instant.now());
    }

    /** Submitted to the provider; awaiting the customer/provider outcome. */
    public void submit(String providerReference) {
        transitionTo(PaymentStatus.PENDING);
        this.providerReference = Objects.requireNonNull(providerReference, "providerReference");
    }

    public void authorize() {
        transitionTo(PaymentStatus.AUTHORIZED);
    }

    public void markSucceeded() {
        transitionTo(PaymentStatus.SUCCESS);
    }

    public void fail(String reason) {
        transitionTo(PaymentStatus.FAILED);
        this.failureReason = reason;
    }

    public void expire() {
        transitionTo(PaymentStatus.EXPIRED);
    }

    public void cancel() {
        transitionTo(PaymentStatus.CANCELLED);
    }

    public void settle() {
        transitionTo(PaymentStatus.SETTLED);
    }

    public void refund() {
        transitionTo(PaymentStatus.REFUNDED);
    }

    private void transitionTo(PaymentStatus target) {
        if (!status.canTransitionTo(target)) {
            throw new BusinessRuleViolationException(
                    "INVALID_PAYMENT_TRANSITION",
                    "Cannot move payment from " + status + " to " + target);
        }
        this.status = target;
    }

    @Override
    public UUID id() {
        return id;
    }

    public UUID merchantId() {
        return merchantId;
    }

    public UUID orderId() {
        return orderId;
    }

    public PaymentMethod method() {
        return method;
    }

    public Money amount() {
        return amount;
    }

    public PaymentStatus status() {
        return status;
    }

    public String providerReference() {
        return providerReference;
    }

    public String failureReason() {
        return failureReason;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
