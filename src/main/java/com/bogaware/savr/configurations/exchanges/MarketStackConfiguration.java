package com.bogaware.savr.configurations.exchanges;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties("market-stack")
public class MarketStackConfiguration {
    private String apiHost;
    private String apiKey;
    private List<String> availableExchanges;
}
