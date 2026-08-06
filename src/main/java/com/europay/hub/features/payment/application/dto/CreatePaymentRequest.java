package com.europay.hub.features.payment.application.dto;

import com.europay.hub.features.payment.domain.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/** Create a payment for an existing order using a chosen method. */
public record CreatePaymentRequest(

        @NotNull
        UUID orderId,

        @NotNull
        PaymentMethod paymentMethod) {
}
