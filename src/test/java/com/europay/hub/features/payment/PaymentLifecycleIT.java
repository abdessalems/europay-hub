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
@DisplayName("Payment lifecycle (integration)")
class PaymentLifecycleIT extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private JsonNode data(MvcResult r) throws Exception {
        return objectMapper.readTree(r.getResponse().getContentAsString()).at("/data");
    }

    private String auth(String email) throws Exception {
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"legalName":"Shop","email":"%s","password":"Sup3rSecret!"}
                        """.formatted(email))).andExpect(status().isCreated());
        MvcResult login = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email":"%s","password":"Sup3rSecret!"}
                        """.formatted(email))).andReturn();
        return "Bearer " + data(login).at("/accessToken").asText();
    }

    private String order(String auth) throws Exception {
        return data(mockMvc.perform(post("/api/orders").header("Authorization", auth)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"customer":{"email":"b@x.eu","fullName":"B"},"amount":"49.99"}
                        """)).andReturn()).at("/id").asText();
    }

    private String pay(String auth, String orderId) throws Exception {
        return data(mockMvc.perform(post("/api/payments").header("Authorization", auth)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"orderId":"%s","paymentMethod":"WERO"}
                        """.formatted(orderId))).andReturn()).at("/id").asText();
    }

    @Test
    @DisplayName("approve → SUCCESS marks the order PAID, then refund → REFUNDED")
    void approveThenRefund() throws Exception {
        String auth = auth("lifecycle-1@shop.eu");
        String orderId = order(auth);
        String paymentId = pay(auth, orderId);

        // Approve → SUCCESS
        mockMvc.perform(post("/api/payments/{id}/approve", paymentId).header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("SUCCESS"));

        // Order is now PAID
        mockMvc.perform(get("/api/orders/{id}", orderId).header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PAID"));

        // Refund → REFUNDED
        mockMvc.perform(post("/api/payments/{id}/refund", paymentId).header("Authorization", auth)
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"reason":"customer request"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("REFUNDED"));

        // Refund again → not allowed
        mockMvc.perform(post("/api/payments/{id}/refund", paymentId).header("Authorization", auth))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code").value("REFUND_NOT_ALLOWED"));
    }

    @Test
    @DisplayName("cancel a pending payment; retry is rejected when not FAILED")
    void cancelAndRetryGuards() throws Exception {
        String auth = auth("lifecycle-2@shop.eu");
        String paymentId = pay(auth, order(auth));

        mockMvc.perform(post("/api/payments/{id}/retry", paymentId).header("Authorization", auth))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code").value("RETRY_NOT_ALLOWED"));

        mockMvc.perform(post("/api/payments/{id}/cancel", paymentId).header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));
    }
}
