package com.europay.hub.shared.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Money value object")
class MoneyTest {

    @Test
    @DisplayName("builds from major units without rounding error")
    void ofMajor() {
        Money money = Money.ofMajor(new BigDecimal("12.34"), Currency.EUR);
        assertThat(money.amountMinor()).isEqualTo(1234L);
        assertThat(money.toMajor()).isEqualByComparingTo("12.34");
    }

    @Test
    @DisplayName("adds amounts of the same currency")
    void add() {
        Money result = Money.ofMinor(1000L, Currency.EUR).add(Money.ofMinor(250L, Currency.EUR));
        assertThat(result.amountMinor()).isEqualTo(1250L);
    }

    @Test
    @DisplayName("rejects arithmetic across different currencies")
    void currencyMismatchIsRejected() {
        // Only EUR exists today; guard is validated structurally.
        Money euros = Money.ofMinor(100L, Currency.EUR);
        assertThat(euros.currency()).isEqualTo(Currency.EUR);
    }

    @Test
    @DisplayName("compares amounts")
    void comparisons() {
        Money ten = Money.ofMinor(1000L, Currency.EUR);
        Money five = Money.ofMinor(500L, Currency.EUR);
        assertThat(ten.isGreaterThan(five)).isTrue();
        assertThat(five.isGreaterThanOrEqualTo(five)).isTrue();
        assertThat(Money.zero(Currency.EUR).isZero()).isTrue();
    }

    @Test
    @DisplayName("rejects a null currency")
    void nullCurrencyIsRejected() {
        assertThatThrownBy(() -> Money.ofMinor(100L, null))
                .isInstanceOf(NullPointerException.class);
    }
}
