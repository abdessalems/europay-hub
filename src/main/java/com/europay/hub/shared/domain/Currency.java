package com.europay.hub.shared.domain;

/**
 * Supported ISO-4217 currencies. Only EUR is supported initially (business rule),
 * but the platform is modelled to add more without changing the {@link Money} contract.
 */
public enum Currency {

    EUR(2);

    private final int minorUnits;

    Currency(int minorUnits) {
        this.minorUnits = minorUnits;
    }

    /** Number of decimal places in the fractional (minor) unit, e.g. 2 for EUR (cents). */
    public int minorUnits() {
        return minorUnits;
    }
}
