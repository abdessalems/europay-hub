package com.europay.hub.features.payment.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PaymentStatus transitions")
class PaymentStatusTest {

    @Test
    @DisplayName("allows the happy-path transitions")
    void happyPath() {
        assertThat(PaymentStatus.CREATED.canTransitionTo(PaymentStatus.PENDING)).isTrue();
        assertThat(PaymentStatus.PENDING.canTransitionTo(PaymentStatus.AUTHORIZED)).isTrue();
        assertThat(PaymentStatus.AUTHORIZED.canTransitionTo(PaymentStatus.SUCCESS)).isTrue();
        assertThat(PaymentStatus.SUCCESS.canTransitionTo(PaymentStatus.SETTLED)).isTrue();
        assertThat(PaymentStatus.SUCCESS.canTransitionTo(PaymentStatus.REFUNDED)).isTrue();
    }

    @Test
    @DisplayName("forbids illegal jumps")
    void illegalJumps() {
        assertThat(PaymentStatus.CREATED.canTransitionTo(PaymentStatus.SUCCESS)).isFalse();
        assertThat(PaymentStatus.CREATED.canTransitionTo(PaymentStatus.REFUNDED)).isFalse();
        assertThat(PaymentStatus.SUCCESS.canTransitionTo(PaymentStatus.PENDING)).isFalse();
    }

    @Test
    @DisplayName("identifies terminal states")
    void terminalStates() {
        assertThat(PaymentStatus.EXPIRED.isTerminal()).isTrue();
        assertThat(PaymentStatus.CANCELLED.isTerminal()).isTrue();
        assertThat(PaymentStatus.REFUNDED.isTerminal()).isTrue();
        assertThat(PaymentStatus.PENDING.isTerminal()).isFalse();
    }
}
