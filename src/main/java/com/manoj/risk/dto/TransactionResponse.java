package com.manoj.risk.dto;

import com.manoj.risk.model.RiskLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

public class TransactionResponse {
  @Schema(description = "Transaction identifier assigned by the system", example = "txn-12345")
  private final String transactionId;
  @Schema(description = "Calculated risk score for the transaction", example = "72")
  private final int riskScore;
  @Schema(description = "Risk level classification", example = "HIGH")
  private final RiskLevel riskLevel;
  @Schema(description = "Narrative reason for the risk assessment", example = "High amount in a high-risk country")
  private final String reason;
  @Schema(description = "Timestamp when the assessment completed", example = "2024-08-21T12:31:00Z")
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
