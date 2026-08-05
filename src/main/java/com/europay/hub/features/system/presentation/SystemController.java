package com.europay.hub.features.system.presentation;

import com.europay.hub.shared.web.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lightweight service metadata endpoint (distinct from Actuator's operational health).
 * Serves as the Phase 0 "it runs" smoke endpoint.
 */
@RestController
@RequestMapping("/api/system")
@Tag(name = "System", description = "Service metadata")
public class SystemController {

    private final String applicationName;
    private final String version;

    public SystemController(
            @Value("${spring.application.name:europay-hub}") String applicationName,
            @Value("${europay.version:0.1.0-SNAPSHOT}") String version) {
        this.applicationName = applicationName;
        this.version = version;
    }

    @GetMapping("/info")
    @Operation(summary = "Service info", description = "Returns application name, version and status.")
    public ApiResponse<Map<String, Object>> info() {
        return ApiResponse.ok(Map.of(
                "application", applicationName,
                "version", version,
                "status", "UP",
                "timestamp", Instant.now().toString()));
    }
}
