package com.europay.hub.features.customer.domain;

import com.europay.hub.shared.domain.PageResult;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

    Customer save(Customer customer);

    Optional<Customer> findById(UUID id);

    Optional<Customer> findByMerchantIdAndEmail(UUID merchantId, String email);

    PageResult<Customer> findByMerchantId(UUID merchantId, int page, int size);
}
