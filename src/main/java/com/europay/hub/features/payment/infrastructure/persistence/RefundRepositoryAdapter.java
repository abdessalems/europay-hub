package com.europay.hub.features.payment.infrastructure.persistence;

import com.europay.hub.features.payment.domain.Refund;
import com.europay.hub.features.payment.domain.RefundRepository;
import com.europay.hub.shared.domain.Currency;
import com.europay.hub.shared.domain.Money;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class RefundRepositoryAdapter implements RefundRepository {

    private final RefundJpaRepository jpa;

    public RefundRepositoryAdapter(RefundJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Refund save(Refund refund) {
        return toDomain(jpa.save(toEntity(refund)));
    }

    @Override
    public List<Refund> findByPaymentId(UUID paymentId) {
        return jpa.findByPaymentId(paymentId).stream().map(RefundRepositoryAdapter::toDomain).toList();
    }

    private static RefundEntity toEntity(Refund r) {
        RefundEntity e = new RefundEntity();
        e.setId(r.id());
        e.setPaymentId(r.paymentId());
        e.setMerchantId(r.merchantId());
        e.setAmountMinor(r.amount().amountMinor());
        e.setCurrency(r.amount().currency().name());
        e.setReason(r.reason());
        e.setCreatedAt(r.createdAt());
        return e;
    }

    private static Refund toDomain(RefundEntity e) {
        Money amount = Money.ofMinor(e.getAmountMinor(), Currency.valueOf(e.getCurrency()));
        return new Refund(e.getId(), e.getPaymentId(), e.getMerchantId(), amount, e.getReason(), e.getCreatedAt());
    }
}
