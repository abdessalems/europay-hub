package com.europay.hub.features.dashboard;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.europay.hub.integration.AbstractIntegrationTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
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
@DisplayName("Dashboard metrics & audit log (integration)")
class DashboardAndAuditIT extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private JsonNode data(MvcResult r) throws Exception {
        return objectMapper.readTree(r.getResponse().getContentAsString()).at("/data");
    }

    @Test
    @DisplayName("a captured payment shows in /dashboard and writes audit entries")
    void dashboardAndAudit() throws Exception {
        String email = "metrics-owner@shop.eu";
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"legalName":"Metrics BV","email":"%s","password":"Sup3rSecret!"}
                        """.formatted(email))).andExpect(status().isCreated());
        String token = "Bearer " + data(mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email":"%s","password":"Sup3rSecret!"}
                        """.formatted(email))).andReturn()).at("/accessToken").asText();

        String orderId = data(mockMvc.perform(post("/api/orders").header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"customer":{"email":"c@x.eu","fullName":"C"},"amount":"80.00"}
                        """)).andReturn()).at("/id").asText();
        String paymentId = data(mockMvc.perform(post("/api/payments").header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"orderId":"%s","paymentMethod":"VISA"}
                        """.formatted(orderId))).andReturn()).at("/id").asText();
        mockMvc.perform(post("/api/payments/{id}/approve", paymentId).header("Authorization", token))
                .andExpect(status().isOk());

        // Dashboard reflects the captured payment
        mockMvc.perform(get("/api/dashboard").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderCount").value(1))
                .andExpect(jsonPath("$.data.paymentCount").value(1))
                .andExpect(jsonPath("$.data.revenue").value(80.00))
                .andExpect(jsonPath("$.data.successRate").value(100));

        // Audit captured the login, order, and payment success
        MvcResult audit = mockMvc.perform(get("/api/audit-logs?size=50").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[*].action", Matchers.hasItems(
                        "USER_LOGIN", "ORDER_CREATED", "PAYMENT_SUCCESS")))
                .andReturn();
        // scoped to this merchant only
        assert audit.getResponse().getContentAsString().contains("ORDER_CREATED");
    }
}
