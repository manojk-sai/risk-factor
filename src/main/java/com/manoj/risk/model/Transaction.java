package com.manoj.risk.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "transactions")
public class Transaction {
  @Id
  private String id;
  private String customerId;
  private BigDecimal amount;
  private String currency;
  private String country;
  private String merchantCategory;
  private Instant timestamp;

  public Transaction(
      String id,
      String customerId,
      BigDecimal amount,
      String currency,
      String country,
      String merchantCategory,
      Instant timestamp) {
    this.id = id;
    this.customerId = customerId;
    this.amount = amount;
    this.currency = currency;
    this.country = country;
    this.merchantCategory = merchantCategory;
    this.timestamp = timestamp;
  }

  public String getId() {
    return id;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Transaction that = (Transaction) o;
    return Objects.equals(id, that.id)
        && Objects.equals(customerId, that.customerId)
        && Objects.equals(amount, that.amount)
        && Objects.equals(currency, that.currency)
        && Objects.equals(country, that.country)
        && Objects.equals(merchantCategory, that.merchantCategory)
        && Objects.equals(timestamp, that.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, customerId, amount, currency, country, merchantCategory, timestamp);
  }
}
