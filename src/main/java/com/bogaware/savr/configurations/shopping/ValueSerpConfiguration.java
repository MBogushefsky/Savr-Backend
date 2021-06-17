package com.bogaware.savr.configurations.shopping;

import com.ebay.api.client.auth.oauth2.CredentialUtil;
import com.ebay.api.client.auth.oauth2.OAuth2Api;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Configuration
@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties("value-serp")
public class ValueSerpConfiguration extends HttpServlet {

    private String apiUrl;
    private String apiKey;
}
