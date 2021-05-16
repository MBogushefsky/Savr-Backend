package com.bogaware.savr.configurations.bank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties("plaid")
public class PlaidConfiguration {
    private String clientId;
    private String secret;
}
