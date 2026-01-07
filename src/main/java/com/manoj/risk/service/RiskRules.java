package com.manoj.risk.service;

import com.manoj.risk.model.CustomerRiskProfile;
import com.manoj.risk.model.RiskLevel;
import com.manoj.risk.model.Transaction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;
@Component
public class RiskRules {
  private  final Set<String> highRiskCountries;
  private  final Set<String>highRiskMerchants;
  private final BigDecimal highAmountThreshold;
  private final BigDecimal spikeMultiplier;
  private final BigDecimal lowSpendMultiplier;

  public RiskRules(RiskRulesProperties properties){
    this.highAmountThreshold = properties.getHighAmountThreshold();
    this.highRiskCountries = properties.getHighRiskCountries();
    this.highRiskMerchants = properties.getHighRiskMerchantCategories();
    this.lowSpendMultiplier = properties.getLowSpendMultiplier();
    this.spikeMultiplier = properties.getSpikeMultiplier();
  }
  public RiskAssessmentResult evaluate(Transaction transaction, CustomerRiskProfile profile) {
            int score = 0;
            StringBuilder reason = new StringBuilder();

            if (transaction.getAmount().compareTo(highAmountThreshold) >=0) {
              score += 40;
              reason.append("High amount. ");
            }
            if (highRiskCountries.contains(transaction.getCountry())) {
              score += 35;
              reason.append("High-risk country. ");
            }
            if (highRiskMerchants.contains(transaction.getMerchantCategory())) {
              score += 30;
              reason.append("High-risk merchant category. ");
            }
            if(profile!=null){
                BigDecimal avgAmount = profile.getAvgAmount();
                if (avgAmount != null && avgAmount.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal spikeThreshold = avgAmount.multiply(spikeMultiplier);
                    if (transaction.getAmount().compareTo(spikeThreshold) > 0) {
                    score += 40;
                    reason.append("Spending spike over average. ");
                    }
                    BigDecimal lowSpendThreshold = avgAmount.multiply(lowSpendMultiplier);
                    if (transaction.getAmount().compareTo(lowSpendThreshold) >= 0
                    && avgAmount.compareTo(transaction.getAmount()) < 0) {
                    score += 35;
                    reason.append("Large amount compared to customer average. ");
                    }
                }
                if(profile.getLastRiskLevel() == RiskLevel.HIGH) {
                    score += 30;
                    reason.append("Customer has previous high risk transaction. ");
                }
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
