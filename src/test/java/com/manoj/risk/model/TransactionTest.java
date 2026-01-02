package com.manoj.risk.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class TransactionTest {

  @Test
  void equalsAndHashCodeMatchForSameValues() {
    Transaction first =
        new Transaction(
            "tx-1",
            "cust-1",
            new BigDecimal("25"),
            "USD",
            "US",
            "GROCERY",
            Instant.parse("2024-01-01T10:00:00Z"));
    Transaction second =
        new Transaction(
            "tx-1",
            "cust-1",
            new BigDecimal("25"),
            "USD",
            "US",
            "GROCERY",
            Instant.parse("2024-01-01T10:00:00Z"));

    assertThat(first).isEqualTo(second);
    assertThat(first.hashCode()).isEqualTo(second.hashCode());
  }

  @Test
  void notEqualWhenDifferentId() {
    Transaction first =
        new Transaction(
            "tx-1",
            "cust-1",
            new BigDecimal("25"),
            "USD",
            "US",
            "GROCERY",
            Instant.parse("2024-01-01T10:00:00Z"));
    Transaction second =
        new Transaction(
            "tx-2",
            "cust-1",
            new BigDecimal("25"),
            "USD",
            "US",
            "GROCERY",
            Instant.parse("2024-01-01T10:00:00Z"));

    assertThat(first).isNotEqualTo(second);
  }
}
