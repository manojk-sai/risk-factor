package com.manoj.risk.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.manoj.risk.model.RiskAssessment;
import com.manoj.risk.model.CustomerRiskProfile;
import com.manoj.risk.model.RiskLevel;
import com.manoj.risk.model.Transaction;
import com.manoj.risk.repository.CustomerRiskProfileRepository;
import com.manoj.risk.repository.RiskAssessmentRepository;
import java.math.BigDecimal;
import java.time.Instant;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
@Import(RiskScoringServiceTest.TestConfig.class)
@SpringBootTest
class RiskScoringServiceTest {
  @Autowired
  private RiskScoringService service;
  @Autowired
  private MeterRegistry meterRegistry;
  @MockitoBean
    private RiskAssessmentRepository repository;
  @MockitoBean
  private CustomerRiskProfileRepository customerRiskProfileRepository;
  @MockitoBean
  private RiskRules riskRules;
  static class TestConfig {
    @Bean
    public MeterRegistry meterRegistry() {
      return new SimpleMeterRegistry();
    }
  }
  @Test
  void assessPersistsRiskAssessment() {
    Transaction transaction =
        new Transaction(
            "tx-10",
            "cust-10",
            new BigDecimal("8000"),
            "USD",
            "US",
            "GAMBLING",
            Instant.parse("2024-01-05T12:00:00Z"));
    CustomerRiskProfile profile =
        new CustomerRiskProfile(
            "cust-10",
            RiskLevel.MEDIUM,
            new BigDecimal("150.00"),
            1L,
            Instant.parse("2024-01-01T10:00:00Z"));

    RiskRules.RiskAssessmentResult assessmentResult =
        new RiskRules.RiskAssessmentResult(70, RiskLevel.HIGH, "High amount.");

    RiskAssessment savedAssessment =
        new RiskAssessment(
            "ra-1",
            transaction.getId(),
            70,
            RiskLevel.HIGH,
            "High amount.",
            Instant.parse("2024-01-05T12:05:00Z"));
    when(customerRiskProfileRepository.findByCustomerId("cust-10")).thenReturn(profile);
    when(riskRules.evaluate(transaction, profile)).thenReturn(assessmentResult);
    when(repository.save(any(RiskAssessment.class))).thenReturn(savedAssessment);

    RiskAssessment result = service.assess(transaction);

    assertThat(result).isEqualTo(savedAssessment);
    verify(repository).save(any(RiskAssessment.class));
  }

  @Test
  void assessPropagatesFailuresAndIncrementsCounter() {
    Transaction transaction =
        new Transaction(
            "tx-11",
            "cust-11",
            new BigDecimal("250"),
            "USD",
            "US",
            "GROCERY",
            Instant.parse("2024-01-06T12:00:00Z"));

    when(customerRiskProfileRepository.findByCustomerId("cust-11")).thenReturn(null);
    when(riskRules.evaluate(transaction, null)).thenThrow(new RuntimeException("boom"));

    double beforeCount = meterRegistry.counter("risk_assessment_failed_total").count();

    assertThatThrownBy(() -> service.assess(transaction))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Risk assessment failed");

    assertThat(meterRegistry.counter("risk_assessment_failed_total").count())
        .isEqualTo(beforeCount + 1);
  }
}
