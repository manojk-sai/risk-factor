package com.manoj.risk.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.manoj.risk.model.CustomerRiskProfile;
import com.manoj.risk.model.RiskLevel;
import com.manoj.risk.model.Transaction;
import java.math.BigDecimal;
import java.time.Instant;

import com.manoj.risk.repository.CustomerRiskProfileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class RiskRulesTest {
  @Autowired
  private RiskRules rules;
  @MockitoBean
    private CustomerRiskProfileRepository customerRiskProfileRepository;

  @Test
  void evaluatesHighRiskTransaction() {
    Transaction transaction =
        new Transaction(
            "tx-1",
            "cust-1",
            new BigDecimal("6000"),
            "USD",
            "IR",
            "CRYPTO",
            Instant.parse("2024-01-01T10:00:00Z"));

    when(customerRiskProfileRepository.findByCustomerId("cust-1")).thenReturn(null);
    RiskRules.RiskAssessmentResult result = rules.evaluate(transaction,  customerRiskProfileRepository.findByCustomerId(transaction.getCustomerId()));

    assertThat(result.score()).isGreaterThanOrEqualTo(65);
    assertThat(result.level()).isEqualTo(RiskLevel.HIGH);
    assertThat(result.reason()).contains("High amount");
  }

  @Test
  void evaluatesLowRiskTransaction() {
    Transaction transaction =
        new Transaction(
            "tx-2",
            "cust-2",
            new BigDecimal("25"),
            "USD",
            "US",
            "GROCERY",
            Instant.parse("2024-01-02T10:00:00Z"));

    when(customerRiskProfileRepository.findByCustomerId("cust-2")).thenReturn(null);
    RiskRules.RiskAssessmentResult result = rules.evaluate(transaction,  customerRiskProfileRepository.findByCustomerId(transaction.getCustomerId()));

    assertThat(result.score()).isZero();
    assertThat(result.level()).isEqualTo(RiskLevel.LOW);
    assertThat(result.reason()).contains("No high-risk indicators");
  }

  @Test
  void evaluatesMediumRiskFromHighAmountOnly() {
    Transaction transaction =
        new Transaction(
            "tx-3",
            "cust-3",
            new BigDecimal("5000"),
            "USD",
            "US",
            "BOOKS",
            Instant.parse("2024-01-03T10:00:00Z"));

    when(customerRiskProfileRepository.findByCustomerId("cust-3")).thenReturn(null);
    RiskRules.RiskAssessmentResult result = rules.evaluate(transaction,  customerRiskProfileRepository.findByCustomerId(transaction.getCustomerId()));

    assertThat(result.score()).isEqualTo(40);
    assertThat(result.level()).isEqualTo(RiskLevel.MEDIUM);
    assertThat(result.reason()).isEqualTo("High amount.");
  }

  @Test
  void aggregatesMultipleRiskIndicators() {
    Transaction transaction =
        new Transaction(
            "tx-4",
            "cust-4",
            new BigDecimal("5200"),
            "USD",
            "KP",
            "CRYPTO",
            Instant.parse("2024-01-04T10:00:00Z"));

    when(customerRiskProfileRepository.findByCustomerId("cust-4")).thenReturn(null);
    RiskRules.RiskAssessmentResult result = rules.evaluate(transaction,  customerRiskProfileRepository.findByCustomerId(transaction.getCustomerId()));

    assertThat(result.score()).isEqualTo(105);
    assertThat(result.level()).isEqualTo(RiskLevel.HIGH);
    assertThat(result.reason())
        .isEqualTo("High amount. High-risk country. High-risk merchant category.");
  }

  @Test
  void evaluatesProfileBasedRiskSignals() {
    Transaction transaction =
        new Transaction(
            "tx-5",
            "cust-5",
            new BigDecimal("400"),
            "USD",
            "US",
            "GROCERY",
            Instant.parse("2024-01-05T10:00:00Z"));
    CustomerRiskProfile profile =
        new CustomerRiskProfile(
            "cust-5",
            RiskLevel.HIGH,
            new BigDecimal("100"),
            4L,
            Instant.parse("2023-12-01T10:00:00Z"));

    RiskRules.RiskAssessmentResult result = rules.evaluate(transaction, profile);

    assertThat(result.score()).isEqualTo(105);
    assertThat(result.level()).isEqualTo(RiskLevel.HIGH);
    assertThat(result.reason()).contains("Spending spike over average.");
    assertThat(result.reason()).contains("Large amount compared to customer average.");
    assertThat(result.reason()).contains("Customer has previous high risk transaction.");
  }
}
