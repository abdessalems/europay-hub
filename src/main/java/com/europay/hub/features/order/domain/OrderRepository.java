package com.europay.hub.features.order.domain;

import com.europay.hub.shared.domain.PageResult;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(UUID id);

    PageResult<Order> findByMerchantId(UUID merchantId, int page, int size);

    PageResult<Order> findByMerchantIdAndCustomerId(UUID merchantId, UUID customerId, int page, int size);

    boolean existsByMerchantIdAndReference(UUID merchantId, String reference);
}
