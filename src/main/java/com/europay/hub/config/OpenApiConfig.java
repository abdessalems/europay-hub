package com.europay.hub.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger UI metadata and the two security schemes:
 * <ul>
 *   <li>{@code bearer-jwt} — JWT for dashboard/user endpoints (the "Authorize" button)</li>
 *   <li>{@code api-key} — {@code X-API-Key} header for merchant server-to-server calls (used from Phase 3)</li>
 * </ul>
 */
@Configuration
public class OpenApiConfig {

    private static final String BEARER_JWT = "bearer-jwt";
    private static final String API_KEY = "api-key";

    @Bean
    public OpenAPI euroPayOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("EuroPay Hub API")
                        .description("A modern European Merchant Payment Platform "
                                + "inspired by Worldline and the European Payments Initiative.")
                        .version("v0.1.0")
                        .contact(new Contact().name("EuroPay Hub"))
                        .license(new License().name("MIT")))
                .components(new Components()
                        .addSecuritySchemes(BEARER_JWT, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Paste the accessToken returned by /api/auth/login"))
                        .addSecuritySchemes(API_KEY, new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-API-Key")
                                .description("Merchant API key for server-to-server calls")));
    }
}
