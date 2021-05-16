package com.bogaware.savr.configurations.investing;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties("binance")
public class BinanceConfiguration {
    private String apiHost;
    private String apiKey;
    private String apiSecret;
}
