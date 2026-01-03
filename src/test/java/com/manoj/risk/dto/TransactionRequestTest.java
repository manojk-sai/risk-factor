package com.manoj.risk.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class TransactionRequestTest {

  @Test
  void exposesProvidedFields() {
    Instant timestamp = Instant.parse("2024-04-01T10:00:00Z");
    TransactionRequest request =
        new TransactionRequest(
            "cust-300", new BigDecimal("75.50"), "EUR", "DE", "TRAVEL", timestamp);

    assertThat(request.getCustomerId()).isEqualTo("cust-300");
    assertThat(request.getAmount()).isEqualTo(new BigDecimal("75.50"));
    assertThat(request.getCurrency()).isEqualTo("EUR");
    assertThat(request.getCountry()).isEqualTo("DE");
    assertThat(request.getMerchantCategory()).isEqualTo("TRAVEL");
    assertThat(request.getTimestamp()).isEqualTo(timestamp);
  }
}
