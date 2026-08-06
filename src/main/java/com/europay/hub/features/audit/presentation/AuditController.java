package com.europay.hub.features.audit.presentation;

import com.europay.hub.features.audit.application.AuditService;
import com.europay.hub.features.audit.application.dto.AuditLogResponse;
import com.europay.hub.security.SecurityUser;
import com.europay.hub.shared.web.ApiResponse;
import com.europay.hub.shared.web.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit-logs")
@Tag(name = "Audit", description = "Append-only log of important actions")
@SecurityRequirement(name = "bearer-jwt")
@PreAuthorize("hasRole('MERCHANT')")
public class AuditController {

    private static final int MAX_PAGE_SIZE = 100;

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    @Operation(summary = "List my audit log", description = "Paginated, newest first.")
    public ApiResponse<PageResponse<AuditLogResponse>> list(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(auditService.list(user.merchantId(), page, Math.min(size, MAX_PAGE_SIZE)));
    }
}
