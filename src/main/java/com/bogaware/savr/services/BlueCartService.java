package com.bogaware.savr.services;

import com.bogaware.savr.configurations.BlueCartConfiguration;
import com.ebay.api.client.auth.oauth2.OAuth2Api;
import com.ebay.api.client.auth.oauth2.model.Environment;
import com.ebay.api.client.auth.oauth2.model.OAuthResponse;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class BlueCartService {

    private OAuth2Api ebayAuthApi;
    private String accessToken;

    @Autowired
    private BlueCartConfiguration blueCartConfiguration;

    @Autowired
    private RestTemplate restTemplate;

    public ObjectNode getProductsByKeyword(String keyword) throws UnsupportedEncodingException {
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("api_key", blueCartConfiguration.getApiKey());
        queryParams.put("type", "search");
        queryParams.put("search_term", keyword);
        HttpEntity<ObjectNode> response = restTemplate.exchange(
                blueCartConfiguration.getApiHost() + "?" + getQueryParamsString(queryParams),
                HttpMethod.GET,
                getHeaders(),
                ObjectNode.class);
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
        return new HttpEntity<>(headers);
    }
}
