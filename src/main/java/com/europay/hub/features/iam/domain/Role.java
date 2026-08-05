package com.europay.hub.features.iam.domain;

/**
 * Authorization role of a dashboard user.
 * <ul>
 *   <li>{@code ADMIN} — internal EuroPay Hub operator, platform-wide access.</li>
 *   <li>{@code MERCHANT} — a merchant's own user, scoped to that merchant's data.</li>
 * </ul>
 */
public enum Role {
    ADMIN,
    MERCHANT
}
