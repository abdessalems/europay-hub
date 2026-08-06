package com.europay.hub.features.dashboard.application;

import com.europay.hub.features.dashboard.application.dto.DashboardMetrics;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

    private final DashboardMetricsRepository repository;

    public DashboardService(DashboardMetricsRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public DashboardMetrics getMetrics(UUID merchantId) {
        return repository.load(merchantId);
    }
}
