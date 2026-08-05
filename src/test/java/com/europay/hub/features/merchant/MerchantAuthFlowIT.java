package com.europay.hub.features.merchant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.europay.hub.integration.AbstractIntegrationTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Merchant & IAM flow (integration)")
class MerchantAuthFlowIT extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("register → login → access /me → create/list/revoke API key")
    void fullFlow() throws Exception {
        String email = "owner@acme-shop.eu";
        String password = "Sup3rSecret!";

        // 1. Register
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"legalName":"Acme Shop BV","email":"%s","password":"%s"}
                                """.formatted(email, password)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.role").value("MERCHANT"));

        // 2. Duplicate registration is rejected (409)
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"legalName":"Acme Shop BV","email":"%s","password":"%s"}
                                """.formatted(email, password)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code").value("EMAIL_ALREADY_IN_USE"));

        // 3. Login
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"%s"}
                                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andReturn();
        String token = json(loginResult).at("/data/accessToken").asText();
        assertThat(token).isNotBlank();

        // 4. Wrong password → 401
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"wrong-password"}
                                """.formatted(email)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code").value("INVALID_CREDENTIALS"));

        // 5. /me without token → 401
        mockMvc.perform(get("/api/merchants/me"))
                .andExpect(status().isUnauthorized());

        // 6. /me with token → 200
        mockMvc.perform(get("/api/merchants/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(email))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));

        // 7. Create an API key → secret returned once
        MvcResult keyResult = mockMvc.perform(post("/api/merchants/me/api-keys")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Production server"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.secretKey").exists())
                .andReturn();
        JsonNode keyData = json(keyResult).at("/data");
        String keyId = keyData.at("/id").asText();
        assertThat(keyData.at("/secretKey").asText()).startsWith("epk_live_");

        // 8. List keys → present, secret NOT exposed
        mockMvc.perform(get("/api/merchants/me/api-keys").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Production server"))
                .andExpect(jsonPath("$.data[0].secretKey").doesNotExist());

        // 9. Revoke → 204
        mockMvc.perform(delete("/api/merchants/me/api-keys/{id}", keyId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    private JsonNode json(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
