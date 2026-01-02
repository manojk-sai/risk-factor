package com.manoj.risk.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

public class TransactionRequest {
  @NotBlank
  private String customerId;

  @NotNull
  @DecimalMin(value = "0.01")
  private BigDecimal amount;

  @NotBlank
  private String currency;

  @NotBlank
  private String country;

  @NotBlank
  private String merchantCategory;

  @NotNull
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
