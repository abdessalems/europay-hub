package com.europay.hub.features.dashboard.application;

import com.europay.hub.features.dashboard.application.dto.DashboardMetrics;
import java.util.UUID;

/** Read-model port for dashboard aggregates. Implemented with SQL aggregates in infrastructure. */
public interface DashboardMetricsRepository {

    DashboardMetrics load(UUID merchantId);
}
