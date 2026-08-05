package com.europay.hub.features.iam.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Merchant self-registration: creates the merchant and its first (owner) user. */
public record RegisterRequest(

        @NotBlank
        @Size(max = 200)
        String legalName,

        @NotBlank
        @Email
        @Size(max = 255)
        String email,

        @NotBlank
        @Size(min = 8, max = 100)
        String password) {
}
