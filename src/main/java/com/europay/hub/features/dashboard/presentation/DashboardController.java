package com.europay.hub.features.dashboard.presentation;

import com.europay.hub.features.dashboard.application.DashboardService;
import com.europay.hub.features.dashboard.application.dto.DashboardMetrics;
import com.europay.hub.security.SecurityUser;
import com.europay.hub.shared.web.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Aggregated merchant metrics for the console")
@SecurityRequirement(name = "bearer-jwt")
@PreAuthorize("hasRole('MERCHANT')")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    @Operation(summary = "Get dashboard metrics",
            description = "Revenue, counts, success rate, and chart series (by method, by status, revenue/day).")
    public ApiResponse<DashboardMetrics> metrics(@AuthenticationPrincipal SecurityUser user) {
        return ApiResponse.ok(dashboardService.getMetrics(user.merchantId()));
    }
}
