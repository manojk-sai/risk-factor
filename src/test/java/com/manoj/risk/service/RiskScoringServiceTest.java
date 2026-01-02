package com.manoj.risk.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.manoj.risk.model.RiskAssessment;
import com.manoj.risk.model.RiskLevel;
import com.manoj.risk.model.Transaction;
import com.manoj.risk.repository.RiskAssessmentRepository;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class RiskScoringServiceTest {

  @Test
  void assessPersistsRiskAssessment() {
    RiskAssessmentRepository repository = Mockito.mock(RiskAssessmentRepository.class);
    RiskScoringService service = new RiskScoringService(repository);
    Transaction transaction =
        new Transaction(
            "tx-10",
            "cust-10",
            new BigDecimal("8000"),
            "USD",
            "US",
            "GAMBLING",
            Instant.parse("2024-01-05T12:00:00Z"));

    RiskAssessment savedAssessment =
        new RiskAssessment(
            "ra-1",
            transaction.getId(),
            70,
            RiskLevel.HIGH,
            "High amount.",
            Instant.parse("2024-01-05T12:05:00Z"));
    when(repository.save(any(RiskAssessment.class))).thenReturn(savedAssessment);

    RiskAssessment result = service.assess(transaction);

    assertThat(result).isEqualTo(savedAssessment);
    verify(repository).save(any(RiskAssessment.class));
  }
}
