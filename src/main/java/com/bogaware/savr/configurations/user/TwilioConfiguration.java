package com.bogaware.savr.configurations.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties("twilio")
public class TwilioConfiguration {
    private String accoundSid;
    private String authToken;
    private String phoneNumber;
    private int transactionsToMessageLimit;
    private int transactionNameContentLimit;
}
