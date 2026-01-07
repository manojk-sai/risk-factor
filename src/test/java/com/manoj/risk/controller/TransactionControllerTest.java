package com.manoj.risk.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.manoj.risk.dto.RiskAssessmentResponse;
import com.manoj.risk.dto.TransactionRequest;
import com.manoj.risk.dto.TransactionSummaryResponse;
import com.manoj.risk.model.RiskAssessment;
import com.manoj.risk.model.RiskLevel;
import com.manoj.risk.model.Transaction;
import com.manoj.risk.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.mockito.Mock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
@Import({TransactionControllerTest.TestConfig.class, TransactionControllerTest.TestConfig.class})
@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean  private TransactionService transactionService;
  static class TestConfig {
    @Bean
    public MeterRegistry meterRegistry() {
      return new SimpleMeterRegistry();
    }
  }
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
        .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.path").value("/transactions"))
        .andExpect(jsonPath("$.message").value("Validation Failed"))
        .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.details.customerId").exists())
            .andExpect(jsonPath("$.details.amount").exists())
            .andExpect(jsonPath("$.details.currency").exists())
            .andExpect(jsonPath("$.details.country").exists())
            .andExpect(jsonPath("$.details.merchantCategory").exists())
            .andExpect(jsonPath("$.details.timestamp").exists());
  }

  @Test
  void listFiltersTransactions() throws Exception {
    Transaction transaction =
        new Transaction(
            "tx-300",
            "cust-300",
            new BigDecimal("300"),
            "USD",
            "US",
            "ELECTRONICS",
            Instant.parse("2024-04-01T10:00:00Z"));
    RiskAssessment assessment =
        new RiskAssessment(
            "ra-300",
            "tx-300",
            80,
            RiskLevel.HIGH,
            "High amount in high-risk country.",
            Instant.parse("2024-04-01T10:01:00Z"));

    TransactionSummaryResponse summaryResponse =
        TransactionSummaryResponse.from(transaction, assessment);
    PageRequest pageRequest = PageRequest.of(0, 5, Sort.by("timestamp").descending());
    when(transactionService.listTransactions(
            eq("cust-300"),
            eq(RiskLevel.HIGH),
           eq(Instant.parse("2024-04-01T00:00:00Z")),
            eq(Instant.parse("2024-04-30T23:59:59Z")),
            eq(pageRequest)))
        .thenReturn(
            new PageImpl<>(
                List.of(summaryResponse), pageRequest,1));
  mockMvc.perform(
          get("/transactions")
              .param("customerId", "cust-300")
              .param("riskLevel", "HIGH")
              .param("fromDate", "2024-04-01T00:00:00Z")
              .param("toDate", "2024-04-30T23:59:59Z")
              .param("page", "0")
              .param("size", "5"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content[0].transactionId").value("tx-300"))
          .andExpect(jsonPath("$.content[0].riskLevel").value("HIGH"))
          .andExpect(jsonPath("$.content[0].riskScore").value(80))
          .andExpect(jsonPath("$.content[0].customerId").value("cust-300"));
  }

  @Test
  void listAssessmentsReturnsSortedList() throws Exception {
    List<RiskAssessmentResponse> responses =
            List.of(
                    new RiskAssessmentResponse(
                            "ra-2",
                            "tx-500",
                            70,
                            RiskLevel.MEDIUM,
                            "Newest assessment",
                            Instant.parse("2024-06-01T10:10:00Z")),
                    new RiskAssessmentResponse(
                            "ra-1",
                            "tx-500",
                            30,
                            RiskLevel.LOW,
                            "Older assessment",
                            Instant.parse("2024-06-01T09:10:00Z")));

    when(transactionService.riskAssessmentResponseList("tx-500")).thenReturn(responses);

    mockMvc
            .perform(get("/transactions/tx-500/assessments"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("ra-2"))
            .andExpect(jsonPath("$[1].id").value("ra-1"))
            .andExpect(jsonPath("$[0].riskLevel").value("MEDIUM"));
  }

  @Test
  void listAssessmentsReturnsEmptyList() throws Exception {
    when(transactionService.riskAssessmentResponseList("tx-501")).thenReturn(List.of());

    mockMvc
            .perform(get("/transactions/tx-501/assessments"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
  }
  }
