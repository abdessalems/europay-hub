package com.europay.hub.features.order.presentation;

import com.europay.hub.features.order.application.OrderService;
import com.europay.hub.features.order.application.dto.CreateOrderRequest;
import com.europay.hub.features.order.application.dto.OrderResponse;
import com.europay.hub.security.SecurityUser;
import com.europay.hub.shared.web.ApiResponse;
import com.europay.hub.shared.web.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Create, view, list and cancel orders")
@SecurityRequirement(name = "bearer-jwt")
@PreAuthorize("hasRole('MERCHANT')")
public class OrderController {

    private static final int MAX_PAGE_SIZE = 100;

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create an order",
            description = "Creates an order for a customer (found or created by email). Amount is in EUR.")
    public ApiResponse<OrderResponse> create(
            @AuthenticationPrincipal SecurityUser user,
            @Valid @RequestBody CreateOrderRequest request) {
        return ApiResponse.ok(orderService.create(user.merchantId(), request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an order by id")
    public ApiResponse<OrderResponse> get(
            @AuthenticationPrincipal SecurityUser user, @PathVariable UUID id) {
        return ApiResponse.ok(orderService.get(user.merchantId(), id));
    }

    @GetMapping
    @Operation(summary = "List orders", description = "Paginated, newest first.")
    public ApiResponse<PageResponse<OrderResponse>> list(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(orderService.list(user.merchantId(), page, Math.min(size, MAX_PAGE_SIZE)));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel an order", description = "Allowed only while the order is CREATED.")
    public ApiResponse<OrderResponse> cancel(
            @AuthenticationPrincipal SecurityUser user, @PathVariable UUID id) {
        return ApiResponse.ok(orderService.cancel(user.merchantId(), id));
    }
}
