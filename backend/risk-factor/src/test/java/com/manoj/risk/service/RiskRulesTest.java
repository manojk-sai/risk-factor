package com.manoj.risk.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.manoj.risk.model.RiskLevel;
import com.manoj.risk.model.Transaction;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class RiskRulesTest {

  @Test
  void evaluatesHighRiskTransaction() {
    RiskRules rules = new RiskRules();
    Transaction transaction =
        new Transaction(
            "tx-1",
            "cust-1",
            new BigDecimal("6000"),
            "USD",
            "IR",
            "CRYPTO",
            Instant.parse("2024-01-01T10:00:00Z"));

    RiskRules.RiskAssessmentResult result = rules.evaluate(transaction);

    assertThat(result.score()).isGreaterThanOrEqualTo(70);
    assertThat(result.level()).isEqualTo(RiskLevel.HIGH);
    assertThat(result.reason()).contains("High amount");
  }

  @Test
  void evaluatesLowRiskTransaction() {
    RiskRules rules = new RiskRules();
    Transaction transaction =
        new Transaction(
            "tx-2",
            "cust-2",
            new BigDecimal("25"),
            "USD",
            "US",
            "GROCERY",
            Instant.parse("2024-01-02T10:00:00Z"));

    RiskRules.RiskAssessmentResult result = rules.evaluate(transaction);

    assertThat(result.score()).isZero();
    assertThat(result.level()).isEqualTo(RiskLevel.LOW);
    assertThat(result.reason()).contains("No high-risk indicators");
  }

  @Test
  void evaluatesMediumRiskFromHighAmountOnly() {
    RiskRules rules = new RiskRules();
    Transaction transaction =
        new Transaction(
            "tx-3",
            "cust-3",
            new BigDecimal("5000"),
            "USD",
            "US",
            "BOOKS",
            Instant.parse("2024-01-03T10:00:00Z"));

    RiskRules.RiskAssessmentResult result = rules.evaluate(transaction);

    assertThat(result.score()).isEqualTo(40);
    assertThat(result.level()).isEqualTo(RiskLevel.MEDIUM);
    assertThat(result.reason()).isEqualTo("High amount.");
  }

  @Test
  void aggregatesMultipleRiskIndicators() {
    RiskRules rules = new RiskRules();
    Transaction transaction =
        new Transaction(
            "tx-4",
            "cust-4",
            new BigDecimal("5200"),
            "USD",
            "KP",
            "CRYPTO",
            Instant.parse("2024-01-04T10:00:00Z"));

    RiskRules.RiskAssessmentResult result = rules.evaluate(transaction);

    assertThat(result.score()).isEqualTo(105);
    assertThat(result.level()).isEqualTo(RiskLevel.HIGH);
    assertThat(result.reason())
        .isEqualTo("High amount. High-risk country. High-risk merchant category.");
  }
}
