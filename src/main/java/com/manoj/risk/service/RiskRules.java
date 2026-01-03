package com.manoj.risk.service;

import com.manoj.risk.model.RiskLevel;
import com.manoj.risk.model.Transaction;
import java.math.BigDecimal;
import java.util.Set;

public class RiskRules {
  private static final Set<String> HIGH_RISK_COUNTRIES = Set.of("IR", "KP", "SY");
  private static final Set<String> HIGH_RISK_MERCHANTS = Set.of("CRYPTO", "GAMBLING");

  public RiskAssessmentResult evaluate(Transaction transaction) {
    int score = 0;
    StringBuilder reason = new StringBuilder();

    if (transaction.getAmount().compareTo(new BigDecimal("5000")) >= 0) {
      score += 40;
      reason.append("High amount. ");
    }
    if (HIGH_RISK_COUNTRIES.contains(transaction.getCountry())) {
      score += 35;
      reason.append("High-risk country. ");
    }
    if (HIGH_RISK_MERCHANTS.contains(transaction.getMerchantCategory())) {
      score += 30;
      reason.append("High-risk merchant category. ");
    }
    if (score == 0) {
      reason.append("No high-risk indicators.");
    }

    RiskLevel level = toLevel(score);
    return new RiskAssessmentResult(score, level, reason.toString().trim());
  }

  private RiskLevel toLevel(int score) {
    if (score >= 70) {
      return RiskLevel.HIGH;
    }
    if (score >= 40) {
      return RiskLevel.MEDIUM;
    }
    return RiskLevel.LOW;
  }

  public record RiskAssessmentResult(int score, RiskLevel level, String reason) {}
}
