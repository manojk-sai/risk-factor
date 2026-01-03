package com.manoj.risk.service;

import com.manoj.risk.model.RiskAssessment;
import com.manoj.risk.model.Transaction;
import com.manoj.risk.repository.RiskAssessmentRepository;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class RiskScoringService {
  private final RiskAssessmentRepository riskAssessmentRepository;
  private final RiskRules riskRules;

  public RiskScoringService(RiskAssessmentRepository riskAssessmentRepository) {
    this.riskAssessmentRepository = riskAssessmentRepository;
    this.riskRules = new RiskRules();
  }

  public RiskAssessment assess(Transaction transaction) {
    RiskRules.RiskAssessmentResult result = riskRules.evaluate(transaction);
    RiskAssessment assessment =
        new RiskAssessment(
            null,
            transaction.getId(),
            result.score(),
            result.level(),
            result.reason(),
            Instant.now());
    return riskAssessmentRepository.save(assessment);
  }
}
