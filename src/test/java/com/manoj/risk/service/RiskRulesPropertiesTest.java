package com.manoj.risk.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.Test;

class RiskRulesPropertiesTest {

  @Test
  void gettersAndSettersRoundTrip() {
    RiskRulesProperties properties = new RiskRulesProperties();
    properties.setHighAmountThreshold(new BigDecimal("5000"));
    properties.setHighRiskCountries(Set.of("IR", "KP"));
    properties.setHighRiskMerchantCategories(Set.of("CRYPTO", "GAMBLING"));
    properties.setLowSpendMultiplier(new BigDecimal("3.0"));
    properties.setSpikeMultiplier(new BigDecimal("2.0"));

    assertThat(properties.getHighAmountThreshold()).isEqualByComparingTo("5000");
    assertThat(properties.getHighRiskCountries()).containsExactlyInAnyOrder("IR", "KP");
    assertThat(properties.getHighRiskMerchantCategories())
        .containsExactlyInAnyOrder("CRYPTO", "GAMBLING");
    assertThat(properties.getLowSpendMultiplier()).isEqualByComparingTo("3.0");
    assertThat(properties.getSpikeMultiplier()).isEqualByComparingTo("2.0");
  }
}
