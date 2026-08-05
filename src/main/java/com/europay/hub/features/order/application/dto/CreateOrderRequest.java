package com.europay.hub.features.order.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Create an order for a customer. The customer is found (by email) or created for this merchant.
 * Amount is in major units (EUR); {@code reference} is optional and generated when omitted.
 */
public record CreateOrderRequest(

        @NotNull
        @Valid
        CustomerInfo customer,

        @NotNull
        @DecimalMin(value = "0.01")
        @Digits(integer = 12, fraction = 2)
        BigDecimal amount,

        @Size(max = 50)
        String reference) {

    public record CustomerInfo(
            @NotBlank @Email @Size(max = 255) String email,
            @NotBlank @Size(max = 200) String fullName) {
    }
}
