package com.europay.hub.features.customer.application;

import com.europay.hub.features.customer.application.dto.CustomerResponse;
import com.europay.hub.features.customer.domain.Customer;
import com.europay.hub.features.customer.domain.CustomerRepository;
import com.europay.hub.features.order.application.dto.OrderResponse;
import com.europay.hub.features.order.domain.OrderRepository;
import com.europay.hub.shared.exception.ResourceNotFoundException;
import com.europay.hub.shared.web.PageResponse;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    public CustomerService(CustomerRepository customerRepository, OrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<CustomerResponse> list(UUID merchantId, int page, int size) {
        return PageResponse.of(customerRepository.findByMerchantId(merchantId, page, size)
                .map(CustomerResponse::from));
    }

    @Transactional(readOnly = true)
    public CustomerResponse get(UUID merchantId, UUID customerId) {
        return CustomerResponse.from(requireOwnedCustomer(merchantId, customerId));
    }

    /** A customer's order history (precursor to payment history). */
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> orders(UUID merchantId, UUID customerId, int page, int size) {
        requireOwnedCustomer(merchantId, customerId);
        return PageResponse.of(orderRepository
                .findByMerchantIdAndCustomerId(merchantId, customerId, page, size)
                .map(OrderResponse::from));
    }

    private Customer requireOwnedCustomer(UUID merchantId, UUID customerId) {
        return customerRepository.findById(customerId)
                .filter(c -> c.merchantId().equals(merchantId))
                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));
    }
}
