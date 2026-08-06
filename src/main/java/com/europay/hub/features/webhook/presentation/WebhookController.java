package com.europay.hub.features.webhook.presentation;

import com.europay.hub.features.webhook.application.WebhookConfigService;
import com.europay.hub.features.webhook.application.WebhookQueryService;
import com.europay.hub.features.webhook.application.dto.ConfigureWebhookRequest;
import com.europay.hub.features.webhook.application.dto.WebhookEndpointResponse;
import com.europay.hub.features.webhook.application.dto.WebhookEventResponse;
import com.europay.hub.security.SecurityUser;
import com.europay.hub.shared.web.ApiResponse;
import com.europay.hub.shared.web.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhooks")
@Tag(name = "Webhooks", description = "Configure the callback endpoint and inspect delivery events")
@SecurityRequirement(name = "bearer-jwt")
@PreAuthorize("hasRole('MERCHANT')")
public class WebhookController {

    private static final int MAX_PAGE_SIZE = 100;

    private final WebhookConfigService configService;
    private final WebhookQueryService queryService;

    public WebhookController(WebhookConfigService configService, WebhookQueryService queryService) {
        this.configService = configService;
        this.queryService = queryService;
    }

    @PutMapping("/endpoint")
    @Operation(summary = "Configure my webhook endpoint",
            description = "Registers or updates the callback URL. The signing secret is returned once here.")
    public ApiResponse<WebhookEndpointResponse> configure(
            @AuthenticationPrincipal SecurityUser user,
            @Valid @RequestBody ConfigureWebhookRequest request) {
        return ApiResponse.ok(configService.configure(user.merchantId(), request));
    }

    @GetMapping("/endpoint")
    @Operation(summary = "Get my webhook endpoint", description = "The secret is masked.")
    public ApiResponse<WebhookEndpointResponse> getEndpoint(@AuthenticationPrincipal SecurityUser user) {
        return ApiResponse.ok(configService.get(user.merchantId()));
    }

    @DeleteMapping("/endpoint")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Disable my webhook endpoint")
    public void disable(@AuthenticationPrincipal SecurityUser user) {
        configService.disable(user.merchantId());
    }

    @GetMapping("/events")
    @Operation(summary = "List webhook events", description = "Delivery status of each event, newest first.")
    public ApiResponse<PageResponse<WebhookEventResponse>> events(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(queryService.listEvents(user.merchantId(), page, Math.min(size, MAX_PAGE_SIZE)));
    }
}
