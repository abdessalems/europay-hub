package com.europay.hub.features.dashboard.application.dto;

import java.math.BigDecimal;
import java.util.List;

/** Server-computed dashboard KPIs and chart series for the current merchant. */
public record DashboardMetrics(
        BigDecimal revenue,
        long orderCount,
        long paymentCount,
        long pendingCount,
        int successRate,
        List<Count> paymentsByMethod,
        List<Count> paymentsByStatus,
        List<DayAmount> revenueByDay) {

    public record Count(String key, long count) {
    }

    public record DayAmount(String date, BigDecimal amount) {
    }
}
