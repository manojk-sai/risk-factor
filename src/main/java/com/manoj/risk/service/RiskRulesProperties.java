package com.manoj.risk.service;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.Set;

@Validated
@Component
@ConfigurationProperties(prefix = "risk.rules")
public class RiskRulesProperties {
    @NotNull
    private BigDecimal highAmountThreshold;
    @NotEmpty
private  Set<String> highRiskCountries;
    @NotEmpty
private Set<String> highRiskMerchantCategories;
    @NotNull
    private BigDecimal spikeMultiplier;
    @NotNull
    private BigDecimal lowSpendMultiplier;

    public BigDecimal getSpikeMultiplier() {
        return spikeMultiplier;
    }
    public void setSpikeMultiplier(BigDecimal spikeMultiplier) {
        this.spikeMultiplier = spikeMultiplier;
    }
    public BigDecimal getLowSpendMultiplier() {
        return lowSpendMultiplier;
    }
    public void setLowSpendMultiplier(BigDecimal lowSpendMultiplier) {
        this.lowSpendMultiplier = lowSpendMultiplier;
    }
    public BigDecimal getHighAmountThreshold() {
        return highAmountThreshold;
    }
    public void setHighAmountThreshold(BigDecimal highAmountThreshold) {
        this.highAmountThreshold = highAmountThreshold;
    }
    public Set<String> getHighRiskCountries() {
        return highRiskCountries;
    }
    public void setHighRiskCountries(Set<String> highRiskCountries) {
        this.highRiskCountries = highRiskCountries;
    }
    public Set<String> getHighRiskMerchantCategories() {
        return highRiskMerchantCategories;
    }
    public void setHighRiskMerchantCategories(Set<String> highRiskMerchantCategories) {
        this.highRiskMerchantCategories = highRiskMerchantCategories;
    }
}
