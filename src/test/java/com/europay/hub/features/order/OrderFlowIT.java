package com.europay.hub.features.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.europay.hub.integration.AbstractIntegrationTest;
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
@DisplayName("Order & Customer flow (integration)")
class OrderFlowIT extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private String registerAndLogin(String email) throws Exception {
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"legalName":"Shop","email":"%s","password":"Sup3rSecret!"}
                                """.formatted(email)))
                .andExpect(status().isCreated());
        MvcResult login = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"Sup3rSecret!"}
                                """.formatted(email)))
                .andExpect(status().isOk()).andReturn();
        return objectMapper.readTree(login.getResponse().getContentAsString()).at("/data/accessToken").asText();
    }

    @Test
    @DisplayName("create → get → enforce max → list → cancel → customer history")
    void fullFlow() throws Exception {
        String token = registerAndLogin("orders-owner@shop.eu");
        String auth = "Bearer " + token;

        // Create an order (customer created on the fly)
        MvcResult created = mockMvc.perform(post("/api/orders").header("Authorization", auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"customer":{"email":"buyer@x.eu","fullName":"Jan Buyer"},"amount":"49.99"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("CREATED"))
                .andExpect(jsonPath("$.data.amount").value(49.99))
                .andExpect(jsonPath("$.data.currency").value("EUR"))
                .andReturn();
        var data = objectMapper.readTree(created.getResponse().getContentAsString()).at("/data");
        String orderId = data.at("/id").asText();
        String customerId = data.at("/customerId").asText();

        // Get it back
        mockMvc.perform(get("/api/orders/{id}", orderId).header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.reference").isNotEmpty());

        // Amount ceiling is enforced (max is 10,000.00)
        mockMvc.perform(post("/api/orders").header("Authorization", auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"customer":{"email":"buyer@x.eu","fullName":"Jan Buyer"},"amount":"10000.01"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code").value("AMOUNT_EXCEEDS_MAX"));

        // List orders (paginated)
        mockMvc.perform(get("/api/orders?page=0&size=10").header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].id").value(orderId));

        // Cancel, then cancelling again is rejected
        mockMvc.perform(post("/api/orders/{id}/cancel", orderId).header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));
        mockMvc.perform(post("/api/orders/{id}/cancel", orderId).header("Authorization", auth))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code").value("ORDER_NOT_CANCELLABLE"));

        // Customer appears in listings and history
        mockMvc.perform(get("/api/customers").header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].email").value("buyer@x.eu"));
        MvcResult history = mockMvc.perform(get("/api/customers/{id}/orders", customerId).header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andReturn();
        assertThat(history.getResponse().getContentAsString()).contains(orderId);
    }
}
