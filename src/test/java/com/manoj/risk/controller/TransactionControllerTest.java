package com.manoj.risk.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.manoj.risk.dto.TransactionRequest;
import com.manoj.risk.model.RiskAssessment;
import com.manoj.risk.model.RiskLevel;
import com.manoj.risk.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private TransactionService transactionService;

  @Test
  void submitReturnsAssessment() throws Exception {
    TransactionRequest request =
        new TransactionRequest(
            "cust-200",
            new BigDecimal("200"),
            "USD",
            "US",
            "GROCERY",
            Instant.parse("2024-03-01T10:00:00Z"));

    RiskAssessment assessment =
        new RiskAssessment(
            "ra-200",
            "tx-200",
            10,
            RiskLevel.LOW,
            "No high-risk indicators.",
            Instant.parse("2024-03-01T10:01:00Z"));

    when(transactionService.submit(any(TransactionRequest.class))).thenReturn(assessment);

    mockMvc
        .perform(
            post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.transactionId").value("tx-200"))
        .andExpect(jsonPath("$.riskScore").value(10))
        .andExpect(jsonPath("$.riskLevel").value("LOW"));
  }

  @Test
  void submitRejectsInvalidRequest() throws Exception {
    String invalidRequest =
        "{\"customerId\":\"\",\"amount\":0,\"currency\":\"\",\"country\":\"\","
            + "\"merchantCategory\":\"\",\"timestamp\":null}";

    mockMvc
        .perform(
            post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
        .andExpect(status().isBadRequest());
  }
}
