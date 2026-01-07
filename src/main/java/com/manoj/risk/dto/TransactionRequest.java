package com.manoj.risk.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;

public class TransactionRequest {
  @NotBlank
  @Schema(description = "Unique identifier for the customer", example = "cust-001")
  private String customerId;

  @NotNull
  @DecimalMin(value = "0.01")
  @Schema(description = "Transaction amount in the transaction currency", example = "149.95")
  private BigDecimal amount;

  @NotBlank
  @Schema(description = "ISO 4217 currency code", example = "USD")
  private String currency;

  @NotBlank
  @Schema(description = "ISO 3166-1 alpha-2 country code", example = "US")
  private String country;

  @NotBlank
  @Schema(description = "Merchant category identifier", example = "ELECTRONICS")
  private String merchantCategory;

  @NotNull
  @Schema(description = "Timestamp the transaction occurred (ISO-8601)", example = "2024-08-21T12:30:00Z")
  private Instant timestamp;

  public TransactionRequest() {}

  public TransactionRequest(
      String customerId,
      BigDecimal amount,
      String currency,
      String country,
      String merchantCategory,
      Instant timestamp) {
    this.customerId = customerId;
    this.amount = amount;
    this.currency = currency;
    this.country = country;
    this.merchantCategory = merchantCategory;
    this.timestamp = timestamp;
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
}
