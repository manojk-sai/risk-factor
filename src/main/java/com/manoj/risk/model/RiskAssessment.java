package com.manoj.risk.model;

import java.time.Instant;
import java.util.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "risk_assessments")
public class RiskAssessment {
  @Id
  private String id;
  private String transactionId;
  private int score;
  private RiskLevel level;
  private String reason;
  private Instant assessedAt;

  public RiskAssessment(
      String id,
      String transactionId,
      int score,
      RiskLevel level,
      String reason,
      Instant assessedAt) {
    this.id = id;
    this.transactionId = transactionId;
    this.score = score;
    this.level = level;
    this.reason = reason;
    this.assessedAt = assessedAt;
  }

  public String getId() {
    return id;
  }

  public String getTransactionId() {
    return transactionId;
  }

  public int getScore() {
    return score;
  }

  public RiskLevel getLevel() {
    return level;
  }

  public String getReason() {
    return reason;
  }

  public Instant getAssessedAt() {
    return assessedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RiskAssessment that = (RiskAssessment) o;
    return score == that.score
        && Objects.equals(id, that.id)
        && Objects.equals(transactionId, that.transactionId)
        && level == that.level
        && Objects.equals(reason, that.reason)
        && Objects.equals(assessedAt, that.assessedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, transactionId, score, level, reason, assessedAt);
  }
}
