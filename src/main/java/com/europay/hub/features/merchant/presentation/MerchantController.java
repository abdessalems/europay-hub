package com.europay.hub.features.merchant.presentation;

import com.europay.hub.features.merchant.application.ApiKeyService;
import com.europay.hub.features.merchant.application.MerchantQueryService;
import com.europay.hub.features.merchant.application.dto.ApiKeyCreatedResponse;
import com.europay.hub.features.merchant.application.dto.ApiKeySummaryResponse;
import com.europay.hub.features.merchant.application.dto.CreateApiKeyRequest;
import com.europay.hub.features.merchant.application.dto.MerchantResponse;
import com.europay.hub.security.SecurityUser;
import com.europay.hub.shared.web.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/merchants")
@Tag(name = "Merchant", description = "Merchant profile and API key management")
@SecurityRequirement(name = "bearer-jwt")
@PreAuthorize("hasRole('MERCHANT')")
public class MerchantController {

    private final MerchantQueryService merchantQueryService;
    private final ApiKeyService apiKeyService;

    public MerchantController(MerchantQueryService merchantQueryService, ApiKeyService apiKeyService) {
        this.merchantQueryService = merchantQueryService;
        this.apiKeyService = apiKeyService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get my merchant profile")
    public ApiResponse<MerchantResponse> me(@AuthenticationPrincipal SecurityUser user) {
        return ApiResponse.ok(merchantQueryService.getById(user.merchantId()));
    }

    @PostMapping("/me/api-keys")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create an API key",
            description = "Generates a new API key. The plaintext secret is returned once and cannot be retrieved again.")
    public ApiResponse<ApiKeyCreatedResponse> createApiKey(
            @AuthenticationPrincipal SecurityUser user,
            @Valid @RequestBody CreateApiKeyRequest request) {
        return ApiResponse.ok(apiKeyService.create(user.merchantId(), request));
    }

    @GetMapping("/me/api-keys")
    @Operation(summary = "List my API keys", description = "Returns key metadata only — never the secret.")
    public ApiResponse<List<ApiKeySummaryResponse>> listApiKeys(@AuthenticationPrincipal SecurityUser user) {
        return ApiResponse.ok(apiKeyService.list(user.merchantId()));
    }

    @DeleteMapping("/me/api-keys/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Revoke an API key")
    public void revokeApiKey(@AuthenticationPrincipal SecurityUser user, @PathVariable UUID id) {
        apiKeyService.revoke(user.merchantId(), id);
    }
}
