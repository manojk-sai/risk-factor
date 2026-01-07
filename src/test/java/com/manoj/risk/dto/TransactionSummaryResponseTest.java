package com.manoj.risk.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.manoj.risk.model.RiskAssessment;
import com.manoj.risk.model.RiskLevel;
import com.manoj.risk.model.Transaction;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class TransactionSummaryResponseTest {

  @Test
  void buildsSummaryFromTransactionAndAssessment() {
    Transaction transaction =
        new Transaction(
            "tx-900",
            "cust-900",
            new BigDecimal("199.99"),
            "USD",
            "US",
            "ELECTRONICS",
            Instant.parse("2024-08-21T12:30:00Z"));
    RiskAssessment assessment =
        new RiskAssessment(
            "ra-900",
            "tx-900",
            72,
            RiskLevel.HIGH,
            "High amount in a high-risk country",
            Instant.parse("2024-08-21T12:31:00Z"));

    TransactionSummaryResponse response = TransactionSummaryResponse.from(transaction, assessment);

    assertThat(response.getTransactionId()).isEqualTo("tx-900");
    assertThat(response.getCustomerId()).isEqualTo("cust-900");
    assertThat(response.getSummary())
        .isEqualTo(
            "Transaction of 199.99 USD for customer cust-900 in country US under category ELECTRONICS");
    assertThat(response.getAmount()).isEqualByComparingTo("199.99");
    assertThat(response.getCurrency()).isEqualTo("USD");
    assertThat(response.getCountry()).isEqualTo("US");
    assertThat(response.getMerchantCategory()).isEqualTo("ELECTRONICS");
    assertThat(response.getTimestamp()).isEqualTo(Instant.parse("2024-08-21T12:30:00Z"));
    assertThat(response.getRiskScore()).isEqualTo(72);
    assertThat(response.getRiskLevel()).isEqualTo(RiskLevel.HIGH);
    assertThat(response.getReason()).isEqualTo("High amount in a high-risk country");
    assertThat(response.getAssessedAt()).isEqualTo(Instant.parse("2024-08-21T12:31:00Z"));
  }
}
