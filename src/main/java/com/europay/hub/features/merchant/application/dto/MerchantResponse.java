package com.europay.hub.features.merchant.application.dto;

import com.europay.hub.features.merchant.domain.Merchant;
import java.time.Instant;
import java.util.UUID;

public record MerchantResponse(
        UUID id,
        String legalName,
        String email,
        String status,
        Instant createdAt) {

    public static MerchantResponse from(Merchant m) {
        return new MerchantResponse(m.id(), m.legalName(), m.email(), m.status().name(), m.createdAt());
    }
}
