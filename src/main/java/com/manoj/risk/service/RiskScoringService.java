package com.manoj.risk.service;

import com.manoj.risk.model.CustomerRiskProfile;
import com.manoj.risk.model.RiskAssessment;
import com.manoj.risk.model.RiskLevel;
import com.manoj.risk.model.Transaction;
import com.manoj.risk.repository.CustomerRiskProfileRepository;
import com.manoj.risk.repository.RiskAssessmentRepository;
import java.time.Instant;

import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RiskScoringService {
  private static final Logger logger = LoggerFactory.getLogger(RiskScoringService.class);
  private final RiskAssessmentRepository riskAssessmentRepository;
  private final CustomerRiskProfileRepository customerRiskProfileRepository;
  private final RiskRules riskRules;
  private final MeterRegistry meterRegistry;

  public RiskScoringService(RiskAssessmentRepository riskAssessmentRepository,
                            CustomerRiskProfileRepository customerRiskProfileRepository,
                            RiskRules riskRules, MeterRegistry meterRegistry) {
    this.riskAssessmentRepository = riskAssessmentRepository;
    this.customerRiskProfileRepository = customerRiskProfileRepository;
    this.riskRules = riskRules;
    this.meterRegistry = meterRegistry;
    this.meterRegistry.counter("risk_assessment_failed_total");
    for (RiskLevel level : RiskLevel.values()) {
      this.meterRegistry.counter("risk_assessment_total", "level", level.name());
    }
  }

  public RiskAssessment assess(Transaction transaction) {
    logger.info("Assessing risk for transaction: {} amount: {} currency: {} country: {}", transaction.getId(), transaction.getAmount(), transaction.getCurrency(), transaction.getCountry());
    try{
    RiskRules.RiskAssessmentResult result = riskRules.evaluate(transaction, customerRiskProfileRepository.findByCustomerId(transaction.getCustomerId()));
    meterRegistry.counter("risk_assessment_total", "level", result.level().name()).increment();
    RiskAssessment assessment =
        new RiskAssessment(
            null,
            transaction.getId(),
            result.score(),
            result.level(),
            result.reason(),
            Instant.now());
    logger .info("Risk assessment completed for transaction: {} score: {} level: {} reason: {}", transaction.getId(), result.score(), result.level(), result.reason());
    return riskAssessmentRepository.save(assessment);
  } catch (Exception ex) {
      logger.error("Risk assessment failed for transaction: {}", transaction.getId(), ex);
      meterRegistry.counter("risk_assessment_failed_total").increment();
      throw new IllegalStateException("Risk assessment failed", ex);
    }
    }
}
