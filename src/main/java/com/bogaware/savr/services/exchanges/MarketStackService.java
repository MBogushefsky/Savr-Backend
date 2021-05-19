package com.bogaware.savr.services.exchanges;

import com.bogaware.savr.configurations.exchanges.MarketStackConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
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

    ObjectMapper objectMapper = new ObjectMapper();

    public ArrayNode searchForSymbol(String query) throws UnsupportedEncodingException {
        ArrayNode responseArray = objectMapper.createArrayNode();
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("access_key", marketStackConfiguration.getApiKey());
        queryParams.put("search", query);
        for (String exchange : marketStackConfiguration.getAvailableExchanges()) {
            queryParams.put("exchange", exchange);
            try {
                HttpEntity<ObjectNode> response = restTemplate.exchange(
                        getUriWithSignature(marketStackConfiguration.getApiHost() + "tickers", queryParams),
                        HttpMethod.GET,
                        getHeaders(),
                        ObjectNode.class);
                responseArray.addAll(response.getBody().withArray("data"));
            }
            catch (HttpClientErrorException.NotFound exception) {
                System.out.println("Count not find symbol in one of the exchanges");
            }
        }
        return responseArray;
    }

    public ObjectNode getTickerData(String symbol, boolean isIntraday, boolean isEod, boolean isLatest) throws UnsupportedEncodingException {
        ObjectNode responseBody = objectMapper.createObjectNode();
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("access_key", marketStackConfiguration.getApiKey());

        String apiUrl = marketStackConfiguration.getApiHost() + "tickers/" + symbol;

        if (isIntraday) { apiUrl += "/intraday"; }
        else if (isEod) { apiUrl += "/eod"; }
        if (isLatest) { apiUrl += "/latest"; }

        for (String exchange : marketStackConfiguration.getAvailableExchanges()) {
            queryParams.put("exchange", exchange);
            try {
                HttpEntity<ObjectNode> response = restTemplate.exchange(
                        getUriWithSignature(apiUrl, queryParams),
                        HttpMethod.GET,
                        getHeaders(),
                        ObjectNode.class);
                responseBody = response.getBody();
            }
            catch (HttpClientErrorException.NotFound exception) {
                System.out.println("Count not find symbol in one of the exchanges");
            }
        }
        return responseBody;
    }

    public ObjectNode getIntradayOfSymbol(String symbol, String interval, Date dateFrom, Date dateTo, Integer limit) throws UnsupportedEncodingException {
        ObjectNode responseBody = objectMapper.createObjectNode();
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("access_key", marketStackConfiguration.getApiKey());
        queryParams.put("symbols", symbol);
        queryParams.put("interval", interval);
        queryParams.put("limit", String.valueOf(limit));
        String apiUrl = marketStackConfiguration.getApiHost() + "intraday";

        if (dateFrom != null && dateTo != null) {
            if (dateFrom.getTime() == dateTo.getTime()) {
                apiUrl += "/" + dateTo.toString();
            }
            else {
                queryParams.put("date_from", dateFrom.toString());
                queryParams.put("date_to", dateTo.toString());
            }
        }

        for (String exchange : marketStackConfiguration.getAvailableExchanges()) {
            queryParams.put("exchange", exchange);
            try {
                HttpEntity<ObjectNode> response = restTemplate.exchange(
                        getUriWithSignature(apiUrl, queryParams),
                        HttpMethod.GET,
                        getHeaders(),
                        ObjectNode.class);
                responseBody = response.getBody();
            }
            catch (HttpClientErrorException.NotFound exception) {
                System.out.println("Count not find symbol in one of the exchanges");
            }
        }
        return responseBody;
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
