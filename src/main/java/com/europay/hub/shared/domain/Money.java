package com.europay.hub.shared.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Money value object. Amounts are stored as an integral number of <em>minor units</em>
 * (e.g. cents for EUR) to avoid any floating-point rounding error — a non-negotiable
 * rule in payment systems. Immutable and self-validating.
 *
 * @param amountMinor amount in minor units (cents). May be negative for internal
 *                    arithmetic; domain aggregates enforce positivity where required.
 * @param currency    the currency; arithmetic across currencies is rejected.
 */
public record Money(long amountMinor, Currency currency) {

    public Money {
        Objects.requireNonNull(currency, "currency must not be null");
    }

    public static Money ofMinor(long amountMinor, Currency currency) {
        return new Money(amountMinor, currency);
    }

    /** Build from a major-unit amount (e.g. {@code 12.34} EUR), rounding half-even. */
    public static Money ofMajor(BigDecimal major, Currency currency) {
        Objects.requireNonNull(major, "amount must not be null");
        Objects.requireNonNull(currency, "currency must not be null");
        long minor = major.movePointRight(currency.minorUnits())
                .setScale(0, RoundingMode.HALF_EVEN)
                .longValueExact();
        return new Money(minor, currency);
    }

    public static Money zero(Currency currency) {
        return new Money(0L, currency);
    }

    public Money add(Money other) {
        requireSameCurrency(other);
        return new Money(Math.addExact(amountMinor, other.amountMinor), currency);
    }

    public Money subtract(Money other) {
        requireSameCurrency(other);
        return new Money(Math.subtractExact(amountMinor, other.amountMinor), currency);
    }

    public boolean isZero() {
        return amountMinor == 0L;
    }

    public boolean isPositive() {
        return amountMinor > 0L;
    }

    public boolean isNegative() {
        return amountMinor < 0L;
    }

    public boolean isGreaterThan(Money other) {
        requireSameCurrency(other);
        return amountMinor > other.amountMinor;
    }

    public boolean isGreaterThanOrEqualTo(Money other) {
        requireSameCurrency(other);
        return amountMinor >= other.amountMinor;
    }

    /** Value expressed in major units (e.g. cents → euros). */
    public BigDecimal toMajor() {
        return BigDecimal.valueOf(amountMinor).movePointLeft(currency.minorUnits());
    }

    private void requireSameCurrency(Money other) {
        Objects.requireNonNull(other, "other must not be null");
        if (currency != other.currency) {
            throw new IllegalArgumentException(
                    "Currency mismatch: %s vs %s".formatted(currency, other.currency));
        }
    }

    @Override
    public String toString() {
        return toMajor().toPlainString() + " " + currency;
    }
}
