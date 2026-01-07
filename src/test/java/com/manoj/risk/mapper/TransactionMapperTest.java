package com.manoj.risk.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.manoj.risk.dto.TransactionRequest;
import com.manoj.risk.model.Transaction;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class TransactionMapperTest {
  private final TransactionMapper mapper = new TransactionMapper();

  @Test
  void mapsRequestIntoTransaction() {
    TransactionRequest request =
        new TransactionRequest(
            "cust-900",
            new BigDecimal("125.50"),
            "USD",
            "US",
            "ELECTRONICS",
            Instant.parse("2024-05-01T10:00:00Z"));

    Transaction transaction = mapper.toTransaction(request);

    assertThat(transaction.getId()).isNull();
    assertThat(transaction.getCustomerId()).isEqualTo("cust-900");
    assertThat(transaction.getAmount()).isEqualByComparingTo("125.50");
    assertThat(transaction.getCurrency()).isEqualTo("USD");
    assertThat(transaction.getCountry()).isEqualTo("US");
    assertThat(transaction.getMerchantCategory()).isEqualTo("ELECTRONICS");
    assertThat(transaction.getTimestamp()).isEqualTo(Instant.parse("2024-05-01T10:00:00Z"));
  }
}
