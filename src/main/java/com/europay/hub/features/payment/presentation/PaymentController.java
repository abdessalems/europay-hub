package com.europay.hub.features.payment.presentation;

import com.europay.hub.features.payment.application.PaymentService;
import com.europay.hub.features.payment.application.dto.CreatePaymentRequest;
import com.europay.hub.features.payment.application.dto.PaymentResponse;
import com.europay.hub.security.SecurityUser;
import com.europay.hub.shared.web.ApiResponse;
import com.europay.hub.shared.web.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payments", description = "Create and query payments (JWT or API key)")
@SecurityRequirement(name = "bearer-jwt")
@SecurityRequirement(name = "api-key")
@PreAuthorize("hasRole('MERCHANT')")
public class PaymentController {

    private static final int MAX_PAGE_SIZE = 100;

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a payment",
            description = "Submits a payment for an order via the chosen method. Pass an "
                    + "Idempotency-Key header to make retries safe — a repeated key returns the original payment.")
    public ApiResponse<PaymentResponse> create(
            @AuthenticationPrincipal SecurityUser user,
            @Parameter(description = "Makes creation idempotent") @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody CreatePaymentRequest request) {
        return ApiResponse.ok(paymentService.create(user.merchantId(), request, idempotencyKey));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a payment (status)")
    public ApiResponse<PaymentResponse> get(
            @AuthenticationPrincipal SecurityUser user, @PathVariable UUID id) {
        return ApiResponse.ok(paymentService.get(user.merchantId(), id));
    }

    @GetMapping
    @Operation(summary = "List payments", description = "Paginated, newest first.")
    public ApiResponse<PageResponse<PaymentResponse>> list(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(paymentService.list(user.merchantId(), page, Math.min(size, MAX_PAGE_SIZE)));
    }
}
