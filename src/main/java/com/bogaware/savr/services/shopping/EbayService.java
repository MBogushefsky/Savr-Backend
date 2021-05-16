package com.bogaware.savr.services.shopping;

import com.bogaware.savr.configurations.shopping.EbayConfiguration;
import com.ebay.api.client.auth.oauth2.OAuth2Api;
import com.ebay.api.client.auth.oauth2.model.Environment;
import com.ebay.api.client.auth.oauth2.model.OAuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class EbayService {

    private OAuth2Api ebayAuthApi;
    private String accessToken;

    @Autowired
    private EbayConfiguration ebayConfiguration;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    public EbayService(OAuth2Api ebayAuthApi) throws IOException {
        this.ebayAuthApi = ebayAuthApi;
        OAuthResponse oAuthResponse = ebayAuthApi.getApplicationToken(Environment.PRODUCTION, Arrays.asList("https://api.ebay.com/oauth/api_scope"));
        accessToken = oAuthResponse.getAccessToken().get().getToken();
    }

    public String getProductsByKeyword(String keyword) throws UnsupportedEncodingException {
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("OPERATION-NAME", "findItemsByKeywords");
        queryParams.put("SERVICE-VERSION", "1.0.0");
        queryParams.put("SECURITY-APPNAME", "Mitchell-Frugal-PRD-b4195c7c8-244f47ca");
        queryParams.put("RESPONSE-DATA-FORMAT", "JSON");
        queryParams.put("REST-PAYLOAD", "");
        queryParams.put("keywords", keyword);
        queryParams.put("paginationInput.entriesPerPage", "20");
        HttpEntity<String> response = restTemplate.exchange(
                ebayConfiguration.getApiHost() + "services/search/FindingService/v1?" + getQueryParamsString(queryParams),
                HttpMethod.GET,
                getHeaders(),
                String.class);
        return response.getBody();
    }

    private String getQueryParamsString(HashMap<String, String> queryParams) throws UnsupportedEncodingException {
        if (queryParams.size() == 0) {
            return "";
        }
        String resultQueryParamsString = "";
        int index = 0;
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            resultQueryParamsString += key + "=" + URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
            if (index != (queryParams.size() - 1)) {
                resultQueryParamsString += "&";
            }
            index++;
        }
        return resultQueryParamsString;
    }

    private HttpEntity<?> getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Authorization", accessToken);
        return new HttpEntity<>(headers);
    }
}
