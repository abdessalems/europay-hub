package com.europay.hub.features.payment;

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
@DisplayName("Payment flow (integration)")
class PaymentFlowIT extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private JsonNode data(MvcResult r) throws Exception {
        return objectMapper.readTree(r.getResponse().getContentAsString()).at("/data");
    }

    private String token(String email) throws Exception {
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"legalName":"Shop","email":"%s","password":"Sup3rSecret!"}
                        """.formatted(email))).andExpect(status().isCreated());
        MvcResult login = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email":"%s","password":"Sup3rSecret!"}
                        """.formatted(email))).andExpect(status().isOk()).andReturn();
        return data(login).at("/accessToken").asText();
    }

    private String createOrder(String auth) throws Exception {
        MvcResult r = mockMvc.perform(post("/api/orders").header("Authorization", auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"customer":{"email":"buyer@x.eu","fullName":"Jan"},"amount":"49.99"}
                                """))
                .andExpect(status().isCreated()).andReturn();
        return data(r).at("/id").asText();
    }

    @Test
    @DisplayName("create (Wero→PENDING), idempotent retry, get, list")
    void createAndIdempotency() throws Exception {
        String auth = "Bearer " + token("pay-owner@shop.eu");
        String orderId = createOrder(auth);

        // Create payment with an Idempotency-Key
        MvcResult first = mockMvc.perform(post("/api/payments").header("Authorization", auth)
                        .header("Idempotency-Key", "key-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"orderId":"%s","paymentMethod":"WERO"}
                                """.formatted(orderId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.providerReference").value(org.hamcrest.Matchers.startsWith("WERO-")))
                .andReturn();
        String paymentId = data(first).at("/id").asText();

        // Same key + same body → same payment (idempotent replay)
        mockMvc.perform(post("/api/payments").header("Authorization", auth)
                        .header("Idempotency-Key", "key-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"orderId":"%s","paymentMethod":"WERO"}
                                """.formatted(orderId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(paymentId));

        // Get + list
        mockMvc.perform(get("/api/payments/{id}", paymentId).header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PENDING"));
        mockMvc.perform(get("/api/payments").header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    @DisplayName("Visa authorizes immediately")
    void visaAuthorizes() throws Exception {
        String auth = "Bearer " + token("visa-owner@shop.eu");
        String orderId = createOrder(auth);

        mockMvc.perform(post("/api/payments").header("Authorization", auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"orderId":"%s","paymentMethod":"VISA"}
                                """.formatted(orderId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("AUTHORIZED"))
                .andExpect(jsonPath("$.data.providerReference").value(org.hamcrest.Matchers.startsWith("VISA-")));
    }

    @Test
    @DisplayName("a payment can be created with an API key (X-API-Key)")
    void payWithApiKey() throws Exception {
        String auth = "Bearer " + token("apikey-owner@shop.eu");

        // Create an API key using the JWT
        MvcResult keyResult = mockMvc.perform(post("/api/merchants/me/api-keys").header("Authorization", auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"server"}
                                """))
                .andExpect(status().isCreated()).andReturn();
        String apiKey = data(keyResult).at("/secretKey").asText();

        String orderId = createOrder(auth);

        // Pay using the API key instead of the JWT
        mockMvc.perform(post("/api/payments").header("X-API-Key", apiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"orderId":"%s","paymentMethod":"BANCONTACT"}
                                """.formatted(orderId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.providerReference").value(org.hamcrest.Matchers.startsWith("BCT-")));

        // Without any credential → 401
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"orderId":"%s","paymentMethod":"BANCONTACT"}
                                """.formatted(orderId)))
                .andExpect(status().isUnauthorized());
    }
}
