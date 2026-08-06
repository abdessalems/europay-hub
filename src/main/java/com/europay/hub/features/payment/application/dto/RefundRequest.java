package com.europay.hub.features.payment.application.dto;

import jakarta.validation.constraints.Size;

/** Optional reason for a refund. */
public record RefundRequest(

        @Size(max = 255)
        String reason) {
}
