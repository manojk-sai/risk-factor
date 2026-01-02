package com.manoj.risk.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class RiskAssessmentTest {

  @Test
  void equalsAndHashCodeMatchForSameValues() {
    RiskAssessment first =
        new RiskAssessment(
            "ra-1",
            "tx-1",
            50,
            RiskLevel.MEDIUM,
            "High amount.",
            Instant.parse("2024-01-01T10:00:00Z"));
    RiskAssessment second =
        new RiskAssessment(
            "ra-1",
            "tx-1",
            50,
            RiskLevel.MEDIUM,
            "High amount.",
            Instant.parse("2024-01-01T10:00:00Z"));

    assertThat(first).isEqualTo(second);
    assertThat(first.hashCode()).isEqualTo(second.hashCode());
  }

  @Test
  void notEqualWhenDifferentScore() {
    RiskAssessment first =
        new RiskAssessment(
            "ra-1",
            "tx-1",
            50,
            RiskLevel.MEDIUM,
            "High amount.",
            Instant.parse("2024-01-01T10:00:00Z"));
    RiskAssessment second =
        new RiskAssessment(
            "ra-1",
            "tx-1",
            10,
            RiskLevel.LOW,
            "No high-risk indicators.",
            Instant.parse("2024-01-01T10:00:00Z"));

    assertThat(first).isNotEqualTo(second);
  }
}
