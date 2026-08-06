package com.europay.hub.features.order.application;

import com.europay.hub.features.customer.domain.Customer;
import com.europay.hub.features.customer.domain.CustomerRepository;
import com.europay.hub.features.order.application.dto.CreateOrderRequest;
import com.europay.hub.features.order.application.dto.OrderResponse;
import com.europay.hub.features.order.domain.Order;
import com.europay.hub.features.order.domain.OrderRepository;
import com.europay.hub.shared.domain.Currency;
import com.europay.hub.shared.domain.Money;
import com.europay.hub.shared.event.AuditEvent;
import com.europay.hub.shared.exception.BusinessRuleViolationException;
import com.europay.hub.shared.exception.ResourceNotFoundException;
import com.europay.hub.shared.web.PageResponse;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Order use cases. Enforces the amount ceiling and EUR-only rule, finds-or-creates the
 * customer, and guarantees a unique reference per merchant.
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ApplicationEventPublisher events;
    private final long maxAmountMinor;

    public OrderService(OrderRepository orderRepository,
                        CustomerRepository customerRepository,
                        ApplicationEventPublisher events,
                        @Value("${europay.payment.max-amount-minor}") long maxAmountMinor) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.events = events;
        this.maxAmountMinor = maxAmountMinor;
    }

    @Transactional
    public OrderResponse create(UUID merchantId, CreateOrderRequest request) {
        Money amount = Money.ofMajor(request.amount(), Currency.EUR);
        if (amount.amountMinor() > maxAmountMinor) {
            throw new BusinessRuleViolationException(
                    "AMOUNT_EXCEEDS_MAX", "Order amount exceeds the maximum of " + maxAmountMinor + " minor units");
        }

        Customer customer = customerRepository
                .findByMerchantIdAndEmail(merchantId, request.customer().email())
                .orElseGet(() -> customerRepository.save(Customer.register(
                        merchantId, request.customer().email(), request.customer().fullName())));

        String reference = resolveReference(merchantId, request.reference());
        Order order = orderRepository.save(Order.create(merchantId, customer.id(), reference, amount));
        events.publishEvent(new AuditEvent(merchantId, "merchant:" + merchantId, "ORDER_CREATED",
                "ORDER", order.id(), Map.of("reference", reference, "amountMinor", amount.amountMinor())));
        return OrderResponse.from(order);
    }

    @Transactional(readOnly = true)
    public OrderResponse get(UUID merchantId, UUID orderId) {
        return OrderResponse.from(requireOwnedOrder(merchantId, orderId));
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> list(UUID merchantId, int page, int size) {
        return PageResponse.of(orderRepository.findByMerchantId(merchantId, page, size).map(OrderResponse::from));
    }

    @Transactional
    public OrderResponse cancel(UUID merchantId, UUID orderId) {
        Order order = requireOwnedOrder(merchantId, orderId);
        order.cancel();
        OrderResponse response = OrderResponse.from(orderRepository.save(order));
        events.publishEvent(new AuditEvent(merchantId, "merchant:" + merchantId, "ORDER_CANCELLED",
                "ORDER", orderId, Map.of()));
        return response;
    }

    private Order requireOwnedOrder(UUID merchantId, UUID orderId) {
        return orderRepository.findById(orderId)
                .filter(o -> o.merchantId().equals(merchantId))
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
    }

    private String resolveReference(UUID merchantId, String requested) {
        String reference = (requested == null || requested.isBlank())
                ? "ORD-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase()
                : requested.trim();
        if (orderRepository.existsByMerchantIdAndReference(merchantId, reference)) {
            throw new BusinessRuleViolationException("REFERENCE_TAKEN", "Order reference already exists: " + reference);
        }
        return reference;
    }
}
