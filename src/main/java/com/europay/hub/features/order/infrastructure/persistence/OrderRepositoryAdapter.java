package com.europay.hub.features.order.infrastructure.persistence;

import com.europay.hub.features.order.domain.Order;
import com.europay.hub.features.order.domain.OrderRepository;
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
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository jpa;

    public OrderRepositoryAdapter(OrderJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Order save(Order order) {
        return toDomain(jpa.save(toEntity(order)));
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return jpa.findById(id).map(OrderRepositoryAdapter::toDomain);
    }

    @Override
    public PageResult<Order> findByMerchantId(UUID merchantId, int page, int size) {
        return toPageResult(jpa.findByMerchantId(merchantId, pageable(page, size)));
    }

    @Override
    public PageResult<Order> findByMerchantIdAndCustomerId(UUID merchantId, UUID customerId, int page, int size) {
        return toPageResult(jpa.findByMerchantIdAndCustomerId(merchantId, customerId, pageable(page, size)));
    }

    @Override
    public boolean existsByMerchantIdAndReference(UUID merchantId, String reference) {
        return jpa.existsByMerchantIdAndReference(merchantId, reference);
    }

    private static PageRequest pageable(int page, int size) {
        return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    private static PageResult<Order> toPageResult(Page<OrderEntity> result) {
        return new PageResult<>(
                result.getContent().stream().map(OrderRepositoryAdapter::toDomain).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    private static OrderEntity toEntity(Order o) {
        OrderEntity e = new OrderEntity();
        e.setId(o.id());
        e.setMerchantId(o.merchantId());
        e.setCustomerId(o.customerId());
        e.setReference(o.reference());
        e.setAmountMinor(o.amount().amountMinor());
        e.setCurrency(o.amount().currency().name());
        e.setStatus(o.status());
        e.setCreatedAt(o.createdAt());
        e.setUpdatedAt(Instant.now());
        return e;
    }

    private static Order toDomain(OrderEntity e) {
        Money amount = Money.ofMinor(e.getAmountMinor(), Currency.valueOf(e.getCurrency()));
        return new Order(e.getId(), e.getMerchantId(), e.getCustomerId(), e.getReference(),
                amount, e.getStatus(), e.getCreatedAt());
    }
}
