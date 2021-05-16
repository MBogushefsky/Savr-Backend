package com.bogaware.savr.services.investing;

import com.bogaware.savr.configurations.investing.MarketStackConfiguration;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class MarketStackService {

    @Autowired
    private MarketStackConfiguration marketStackConfiguration;

    @Autowired
    private RestTemplate restTemplate;

    public ObjectNode getIntradayOfSymbol(String symbol) throws UnsupportedEncodingException {
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("access_key", marketStackConfiguration.getApiKey());
        queryParams.put("symbols", symbol);
        HttpEntity<ObjectNode> response = restTemplate.exchange(
                getUriWithSignature(marketStackConfiguration.getApiHost() + "intraday", queryParams),
                HttpMethod.GET,
                getHeaders(),
                ObjectNode.class);
        ObjectNode responseString = response.getBody();
        return responseString;
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

    private String getUriWithSignature(String uriBase, HashMap<String, String> queryParams) throws UnsupportedEncodingException {
        queryParams.put("access_key", marketStackConfiguration.getApiKey());
        String queryParamsString = getQueryParamsString(queryParams);
        return uriBase + "?" + queryParamsString;
    }

    private HttpEntity<?> getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        return new HttpEntity<>(headers);
    }
}
