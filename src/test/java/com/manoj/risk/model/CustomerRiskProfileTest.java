package com.manoj.risk.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class CustomerRiskProfileTest {

    @Test
    void equalsAndHashCodeMatchForSameValues() {
        CustomerRiskProfile first =
                new CustomerRiskProfile(
                        "cust-1",
                        RiskLevel.LOW,
                        new BigDecimal("25.00"),
                        (long) 2,
                        Instant.parse("2024-01-01T10:00:00Z"));
        CustomerRiskProfile second =
                new CustomerRiskProfile(
                        "cust-1",
                        RiskLevel.LOW,
                        new BigDecimal("25.00"),
                        (long) 2,
                        Instant.parse("2024-01-01T10:00:00Z"));

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    @Test
    void notEqualWhenDifferentCustomerId() {
        CustomerRiskProfile first =
                new CustomerRiskProfile(
                        "cust-1",
                        RiskLevel.LOW,
                        new BigDecimal("25.00"),
                        (long) 2,
                        Instant.parse("2024-01-01T10:00:00Z"));
        CustomerRiskProfile second =
                new CustomerRiskProfile(
                        "cust-2",
                        RiskLevel.LOW,
                        new BigDecimal("25.00"),
                        (long) 2,
                        Instant.parse("2024-01-01T10:00:00Z"));

        assertThat(first).isNotEqualTo(second);
    }
}