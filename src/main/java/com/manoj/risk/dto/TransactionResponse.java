package com.manoj.risk.dto;

import com.manoj.risk.model.RiskLevel;
import java.time.Instant;

public class TransactionResponse {
  private final String transactionId;
  private final int riskScore;
  private final RiskLevel riskLevel;
  private final String reason;
  private final Instant assessedAt;

  public TransactionResponse(
      String transactionId,
      int riskScore,
      RiskLevel riskLevel,
      String reason,
      Instant assessedAt) {
    this.transactionId = transactionId;
    this.riskScore = riskScore;
    this.riskLevel = riskLevel;
    this.reason = reason;
    this.assessedAt = assessedAt;
  }

  public String getTransactionId() {
    return transactionId;
  }

  public int getRiskScore() {
    return riskScore;
  }

  public RiskLevel getRiskLevel() {
    return riskLevel;
  }

  public String getReason() {
    return reason;
  }

  public Instant getAssessedAt() {
    return assessedAt;
  }
}
