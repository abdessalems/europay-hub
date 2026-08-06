package com.europay.hub.features.payment.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.europay.hub.shared.domain.Currency;
import com.europay.hub.shared.domain.Money;
import com.europay.hub.shared.exception.BusinessRuleViolationException;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Payment aggregate")
class PaymentTest {

    private Payment newPayment() {
        return Payment.create(UUID.randomUUID(), UUID.randomUUID(), PaymentMethod.WERO,
                Money.ofMinor(4999, Currency.EUR));
    }

    @Test
    @DisplayName("starts CREATED and moves to PENDING on submit")
    void submit() {
        Payment payment = newPayment();
        assertThat(payment.status()).isEqualTo(PaymentStatus.CREATED);

        payment.submit("WERO-REF-1");
        assertThat(payment.status()).isEqualTo(PaymentStatus.PENDING);
        assertThat(payment.providerReference()).isEqualTo("WERO-REF-1");
    }

    @Test
    @DisplayName("follows PENDING → AUTHORIZED → SUCCESS → REFUNDED")
    void fullLifecycle() {
        Payment payment = newPayment();
        payment.submit("REF");
        payment.authorize();
        payment.markSucceeded();
        assertThat(payment.status()).isEqualTo(PaymentStatus.SUCCESS);
        payment.refund();
        assertThat(payment.status()).isEqualTo(PaymentStatus.REFUNDED);
    }

    @Test
    @DisplayName("rejects an illegal transition")
    void illegalTransition() {
        Payment payment = newPayment(); // CREATED
        assertThatThrownBy(payment::markSucceeded)
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("Cannot move payment");
    }

    @Test
    @DisplayName("records a failure reason")
    void failure() {
        Payment payment = newPayment();
        payment.submit("REF");
        payment.fail("insufficient funds");
        assertThat(payment.status()).isEqualTo(PaymentStatus.FAILED);
        assertThat(payment.failureReason()).isEqualTo("insufficient funds");
    }
}
