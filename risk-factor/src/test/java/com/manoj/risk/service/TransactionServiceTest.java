package com.manoj.risk.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.manoj.risk.dto.TransactionRequest;
import com.manoj.risk.model.RiskAssessment;
import com.manoj.risk.model.RiskLevel;
import com.manoj.risk.model.Transaction;
import com.manoj.risk.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class TransactionServiceTest {

  @Test
  void submitStoresTransactionAndReturnsAssessment() {
    TransactionRepository transactionRepository = Mockito.mock(TransactionRepository.class);
    RiskScoringService riskScoringService = Mockito.mock(RiskScoringService.class);
    TransactionService service = new TransactionService(transactionRepository, riskScoringService);

    TransactionRequest request =
        new TransactionRequest(
            "cust-100",
            new BigDecimal("120"),
            "USD",
            "US",
            "ELECTRONICS",
            Instant.parse("2024-02-01T10:00:00Z"));

    Transaction savedTransaction =
        new Transaction(
            "tx-100",
            request.getCustomerId(),
            request.getAmount(),
            request.getCurrency(),
            request.getCountry(),
            request.getMerchantCategory(),
            request.getTimestamp());

    RiskAssessment assessment =
        new RiskAssessment(
            "ra-100",
            savedTransaction.getId(),
            20,
            RiskLevel.LOW,
            "No high-risk indicators.",
            Instant.parse("2024-02-01T10:01:00Z"));

    when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
    when(riskScoringService.assess(savedTransaction)).thenReturn(assessment);

    RiskAssessment result = service.submit(request);

    assertThat(result).isEqualTo(assessment);
    verify(transactionRepository).save(any(Transaction.class));
    verify(riskScoringService).assess(savedTransaction);
  }
}
