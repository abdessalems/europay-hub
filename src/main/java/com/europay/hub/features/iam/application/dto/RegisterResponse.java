package com.europay.hub.features.iam.application.dto;

import java.util.UUID;

public record RegisterResponse(
        UUID merchantId,
        UUID userId,
        String email,
        String role,
        String status) {
}
