package com.manoj.risk.dto;

import com.manoj.risk.model.RiskAssessment;
import com.manoj.risk.model.RiskLevel;
import com.manoj.risk.model.Transaction;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

public class TransactionSummaryResponse {
    @Schema(description = "Transaction identifier", example = "txn-12345")
    private final String transactionId;
    @Schema(description = "Human-readable transaction summary", example = "Transaction of 149.95 USD for customer cust-001 in country US under category ELECTRONICS")
    private final String summary;
    @Schema(description = "Customer identifier", example = "cust-001")
    private final String customerId;
    @Schema(description = "Transaction amount", example = "149.95")
    private final BigDecimal amount;
    @Schema(description = "ISO 4217 currency code", example = "USD")
    private final String currency;
    @Schema(description = "ISO 3166-1 alpha-2 country code", example = "US")
    private final String country;
    @Schema(description = "Merchant category identifier", example = "ELECTRONICS")
    private final String merchantCategory;
    @Schema(description = "Timestamp of the transaction (ISO-8601)", example = "2024-08-21T12:30:00Z")
    private final Instant timestamp;
    @Schema(description = "Calculated risk score", example = "72")
    private final Integer riskScore;
    @Schema(description = "Risk level classification", example = "HIGH")
    private final RiskLevel riskLevel;
    @Schema(description = "Narrative reason for the risk assessment", example = "High amount in a high-risk country")
    private final String reason;
    @Schema(description = "Timestamp when the assessment completed", example = "2024-08-21T12:31:00Z")
    private final Instant assessedAt;

    public TransactionSummaryResponse(
            String transactionId,
            String summary,
            String customerId,
            BigDecimal amount,
            String currency,
            String country,
            String merchantCategory,
            Instant timestamp,
            Integer riskScore,
            RiskLevel riskLevel,
            String reason,
            Instant assessedAt) {
        this.transactionId = transactionId;
        this.summary = summary;
        this.customerId = customerId;
        this.amount = amount;
        this.currency = currency;
        this.country = country;
        this.merchantCategory = merchantCategory;
        this.timestamp = timestamp;
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.reason = reason;
        this.assessedAt = assessedAt;
    }

    public static TransactionSummaryResponse from(Transaction transaction, RiskAssessment assessment) {
        String summary = String.format("Transaction of %s %s for customer %s in country %s under category %s",
                transaction.getAmount(), transaction.getCurrency(), transaction.getCustomerId(),
                transaction.getCountry(), transaction.getMerchantCategory());
        return new TransactionSummaryResponse(
                transaction.getId(),
                summary,
                transaction.getCustomerId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getCountry(),
                transaction.getMerchantCategory(),
                transaction.getTimestamp(),
                assessment.getScore(),
                assessment.getLevel(),
                assessment.getReason(),
                assessment.getAssessedAt()
        );
    }
    public String getTransactionId() {
        return transactionId;
    }
    public String getSummary() {
        return summary;
}
    public String getCustomerId() {
        return customerId;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public String getCurrency() {
        return currency;
    }
    public String getCountry() {
        return country;
    }
    public String getMerchantCategory() {
        return merchantCategory;
    }
    public Instant getTimestamp() {
        return timestamp;
    }
    public Integer getRiskScore() {
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
