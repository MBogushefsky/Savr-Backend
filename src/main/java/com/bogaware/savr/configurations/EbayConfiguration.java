package com.bogaware.savr.configurations;

import com.ebay.api.client.auth.oauth2.CredentialUtil;
import com.ebay.api.client.auth.oauth2.OAuth2Api;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Configuration
@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties("ebay")
public class EbayConfiguration {

    private String apiHost;

    @Bean
    public OAuth2Api getEbayOAuth() throws FileNotFoundException {
        File configFile = new File(System.getProperty("user.dir") + "/src/main/resources/ebay-config.yml");
        CredentialUtil.load(new FileInputStream(configFile.getAbsoluteFile()));
        return new OAuth2Api();
    }
}
