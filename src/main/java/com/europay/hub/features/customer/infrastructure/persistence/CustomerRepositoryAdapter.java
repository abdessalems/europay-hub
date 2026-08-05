package com.europay.hub.features.customer.infrastructure.persistence;

import com.europay.hub.features.customer.domain.Customer;
import com.europay.hub.features.customer.domain.CustomerRepository;
import com.europay.hub.shared.domain.PageResult;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerRepositoryAdapter implements CustomerRepository {

    private final CustomerJpaRepository jpa;

    public CustomerRepositoryAdapter(CustomerJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Customer save(Customer customer) {
        return toDomain(jpa.save(toEntity(customer)));
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return jpa.findById(id).map(CustomerRepositoryAdapter::toDomain);
    }

    @Override
    public Optional<Customer> findByMerchantIdAndEmail(UUID merchantId, String email) {
        return jpa.findByMerchantIdAndEmail(merchantId, email.trim().toLowerCase())
                .map(CustomerRepositoryAdapter::toDomain);
    }

    @Override
    public PageResult<Customer> findByMerchantId(UUID merchantId, int page, int size) {
        Page<CustomerEntity> result = jpa.findByMerchantId(merchantId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return new PageResult<>(
                result.getContent().stream().map(CustomerRepositoryAdapter::toDomain).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    private static CustomerEntity toEntity(Customer c) {
        CustomerEntity e = new CustomerEntity();
        e.setId(c.id());
        e.setMerchantId(c.merchantId());
        e.setEmail(c.email());
        e.setFullName(c.fullName());
        e.setCreatedAt(c.createdAt());
        e.setUpdatedAt(Instant.now());
        return e;
    }

    private static Customer toDomain(CustomerEntity e) {
        return new Customer(e.getId(), e.getMerchantId(), e.getEmail(), e.getFullName(), e.getCreatedAt());
    }
}
