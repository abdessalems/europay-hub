package com.europay.hub.features.customer.presentation;

import com.europay.hub.features.customer.application.CustomerService;
import com.europay.hub.features.customer.application.dto.CustomerResponse;
import com.europay.hub.features.order.application.dto.OrderResponse;
import com.europay.hub.security.SecurityUser;
import com.europay.hub.shared.web.ApiResponse;
import com.europay.hub.shared.web.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customers", description = "View merchant customers and their order history")
@SecurityRequirement(name = "bearer-jwt")
@PreAuthorize("hasRole('MERCHANT')")
public class CustomerController {

    private static final int MAX_PAGE_SIZE = 100;

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    @Operation(summary = "List customers", description = "Paginated, newest first.")
    public ApiResponse<PageResponse<CustomerResponse>> list(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(customerService.list(user.merchantId(), page, Math.min(size, MAX_PAGE_SIZE)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a customer by id")
    public ApiResponse<CustomerResponse> get(
            @AuthenticationPrincipal SecurityUser user, @PathVariable UUID id) {
        return ApiResponse.ok(customerService.get(user.merchantId(), id));
    }

    @GetMapping("/{id}/orders")
    @Operation(summary = "List a customer's orders")
    public ApiResponse<PageResponse<OrderResponse>> orders(
            @AuthenticationPrincipal SecurityUser user,
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(customerService.orders(user.merchantId(), id, page, Math.min(size, MAX_PAGE_SIZE)));
    }
}
