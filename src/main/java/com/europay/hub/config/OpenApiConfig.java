package com.europay.hub.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger UI metadata. Security schemes (JWT + API key) are added in Phase 1.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI euroPayOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("EuroPay Hub API")
                        .description("A modern European Merchant Payment Platform "
                                + "inspired by Worldline and the European Payments Initiative.")
                        .version("v0.1.0")
                        .contact(new Contact().name("EuroPay Hub"))
                        .license(new License().name("MIT")));
    }
}
