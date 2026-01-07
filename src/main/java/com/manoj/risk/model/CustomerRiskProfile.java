package com.manoj.risk.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Document(collection = "customer_risk_profiles")
public class CustomerRiskProfile {
    @Id
    private final String customerId;
    private final RiskLevel lastRiskLevel;
    private final BigDecimal avgAmount;
    private final Long transactionCount;
    private final Instant lastUpdated;
    public CustomerRiskProfile(String customerId, RiskLevel lastRiskLevel, BigDecimal avgAmount, Long transactionCount, Instant lastUpdated) {
        this.customerId = customerId;
        this.lastRiskLevel = lastRiskLevel;
        this.avgAmount = avgAmount;
        this.transactionCount = transactionCount;
        this.lastUpdated = lastUpdated;
    }
    public String getCustomerId() {
        return customerId;
    }
    public RiskLevel getLastRiskLevel() {
        return lastRiskLevel;
    }
    public BigDecimal getAvgAmount() {
        return avgAmount;
    }
    public Long getTransactionCount() {
        return transactionCount;
    }
    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomerRiskProfile that = (CustomerRiskProfile) o;

        return transactionCount == that.transactionCount &&
                Objects.equals(customerId,that.customerId) &&
                lastRiskLevel == that.lastRiskLevel &&
                Objects.equals(avgAmount, that.avgAmount) &&
                Objects.equals(lastUpdated, that.lastUpdated);
    }
    @Override
    public int hashCode() {
        return Objects.hash(customerId, lastRiskLevel, avgAmount, transactionCount, lastUpdated);
    }
}
