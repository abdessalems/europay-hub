package com.europay.hub.features.payment.infrastructure.persistence;

import com.europay.hub.features.payment.domain.Payment;
import com.europay.hub.features.payment.domain.PaymentRepository;
import com.europay.hub.shared.domain.Currency;
import com.europay.hub.shared.domain.Money;
import com.europay.hub.shared.domain.PageResult;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final PaymentJpaRepository jpa;

    public PaymentRepositoryAdapter(PaymentJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Payment save(Payment payment) {
        return toDomain(jpa.save(toEntity(payment)));
    }

    @Override
    public Optional<Payment> findById(UUID id) {
        return jpa.findById(id).map(PaymentRepositoryAdapter::toDomain);
    }

    @Override
    public PageResult<Payment> findByMerchantId(UUID merchantId, int page, int size) {
        Page<PaymentEntity> result = jpa.findByMerchantId(merchantId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return new PageResult<>(
                result.getContent().stream().map(PaymentRepositoryAdapter::toDomain).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    private static PaymentEntity toEntity(Payment p) {
        PaymentEntity e = new PaymentEntity();
        e.setId(p.id());
        e.setMerchantId(p.merchantId());
        e.setOrderId(p.orderId());
        e.setPaymentMethod(p.method());
        e.setAmountMinor(p.amount().amountMinor());
        e.setCurrency(p.amount().currency().name());
        e.setStatus(p.status());
        e.setProviderReference(p.providerReference());
        e.setFailureReason(p.failureReason());
        e.setCreatedAt(p.createdAt());
        e.setUpdatedAt(Instant.now());
        return e;
    }

    private static Payment toDomain(PaymentEntity e) {
        Money amount = Money.ofMinor(e.getAmountMinor(), Currency.valueOf(e.getCurrency()));
        return new Payment(e.getId(), e.getMerchantId(), e.getOrderId(), e.getPaymentMethod(), amount,
                e.getStatus(), e.getProviderReference(), e.getFailureReason(), e.getCreatedAt());
    }
}
