package com.bogaware.savr.configurations;

import com.ebay.api.client.auth.oauth2.CredentialUtil;
import com.ebay.api.client.auth.oauth2.OAuth2Api;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletContext;
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
        System.out.println("FILE: " + this.getClass().getClassLoader().getResource("ebay-config.yml").getPath());
        File configFile = new File(this.getClass().getClassLoader().getResource("ebay-config.yml").getPath());
        CredentialUtil.load(new FileInputStream(configFile.getAbsoluteFile()));
        return new OAuth2Api();
    }
}
