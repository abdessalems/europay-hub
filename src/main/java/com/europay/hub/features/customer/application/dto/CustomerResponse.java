package com.europay.hub.features.customer.application.dto;

import com.europay.hub.features.customer.domain.Customer;
import java.time.Instant;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String email,
        String fullName,
        Instant createdAt) {

    public static CustomerResponse from(Customer c) {
        return new CustomerResponse(c.id(), c.email(), c.fullName(), c.createdAt());
    }
}
