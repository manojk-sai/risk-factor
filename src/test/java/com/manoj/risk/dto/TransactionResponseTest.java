package com.manoj.risk.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.manoj.risk.model.RiskLevel;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class TransactionResponseTest {

  @Test
  void exposesProvidedFields() {
    Instant assessedAt = Instant.parse("2024-04-02T10:00:00Z");
    TransactionResponse response =
        new TransactionResponse("tx-400", 55, RiskLevel.MEDIUM, "High amount.", assessedAt);

    assertThat(response.getTransactionId()).isEqualTo("tx-400");
    assertThat(response.getRiskScore()).isEqualTo(55);
    assertThat(response.getRiskLevel()).isEqualTo(RiskLevel.MEDIUM);
    assertThat(response.getReason()).isEqualTo("High amount.");
    assertThat(response.getAssessedAt()).isEqualTo(assessedAt);
  }
}
