package com.europay.hub.features.order.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.europay.hub.shared.domain.Currency;
import com.europay.hub.shared.domain.Money;
import com.europay.hub.shared.exception.BusinessRuleViolationException;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Order aggregate")
class OrderTest {

    private Order newOrder() {
        return Order.create(UUID.randomUUID(), UUID.randomUUID(), "ORD-1",
                Money.ofMinor(5000, Currency.EUR));
    }

    @Test
    @DisplayName("is created in CREATED status")
    void createdStatus() {
        assertThat(newOrder().status()).isEqualTo(OrderStatus.CREATED);
    }

    @Test
    @DisplayName("rejects a non-positive amount")
    void rejectsZeroAmount() {
        assertThatThrownBy(() -> Order.create(UUID.randomUUID(), UUID.randomUUID(), "ORD-2",
                Money.zero(Currency.EUR)))
                .isInstanceOf(BusinessRuleViolationException.class);
    }

    @Test
    @DisplayName("can be cancelled while CREATED, but not twice")
    void cancelRules() {
        Order order = newOrder();
        order.cancel();
        assertThat(order.status()).isEqualTo(OrderStatus.CANCELLED);

        assertThatThrownBy(order::cancel)
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("cancelled");
    }

    @Test
    @DisplayName("a paid order cannot be cancelled")
    void paidCannotBeCancelled() {
        Order order = newOrder();
        order.markPaid();
        assertThat(order.status()).isEqualTo(OrderStatus.PAID);
        assertThatThrownBy(order::cancel).isInstanceOf(BusinessRuleViolationException.class);
    }
}
