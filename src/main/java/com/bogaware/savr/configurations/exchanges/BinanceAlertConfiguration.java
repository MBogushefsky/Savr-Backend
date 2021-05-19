package com.bogaware.savr.configurations.exchanges;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties("binance-alert")
public class BinanceAlertConfiguration {
    private double significantPriceChangePercent;
}
