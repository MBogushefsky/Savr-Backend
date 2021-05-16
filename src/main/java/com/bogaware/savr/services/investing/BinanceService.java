package com.bogaware.savr.services.investing;

import com.bogaware.savr.configurations.investing.BinanceConfiguration;
import com.bogaware.savr.models.investing.BinanceBalance;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.hash.Hashing;
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
public class BinanceService {

    @Autowired
    private BinanceConfiguration binanceConfiguration;

    @Autowired
    private RestTemplate restTemplate;

    public List<BinanceBalance> getAccountBalances() throws UnsupportedEncodingException {
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("timestamp", String.valueOf(Calendar.getInstance().getTimeInMillis()));
        HttpEntity<ObjectNode> response = restTemplate.exchange(
                getUriWithSignature(binanceConfiguration.getApiHost() + "api/v3/account", queryParams),
                HttpMethod.GET,
                getHeaders(),
                ObjectNode.class);
        List<BinanceBalance> binanceBalances = new ArrayList<>();
        ArrayNode balanceNodes = (ArrayNode)response.getBody().get("balances");
        for (JsonNode balanceNode : balanceNodes) {
            String assetName = balanceNode.get("asset").asText();
            double amount = balanceNode.get("free").asDouble();
            if (amount != 0) {
                if (assetName.equalsIgnoreCase("USD")) {
                    binanceBalances.add(new BinanceBalance(assetName, amount, amount));
                }
                else {
                    binanceBalances.add(new BinanceBalance(assetName, amount, amount * getSymbolTickerPrice(assetName)));
                }
            }
        }
        return binanceBalances;
    }

    public ArrayNode getCandlestickFor24Hours(String symbol) throws UnsupportedEncodingException {
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("symbol", symbol + "USD");
        Calendar startTime = Calendar.getInstance();
        startTime.add(Calendar.HOUR, -24);
        queryParams.put("startTime", String.valueOf(startTime.getTimeInMillis()));
        queryParams.put("endTime", String.valueOf(Calendar.getInstance().getTimeInMillis()));
        queryParams.put("interval", "1h");
        HttpEntity<ArrayNode> response = restTemplate.exchange(
                binanceConfiguration.getApiHost() + "api/v3/klines?" + getQueryParamsString(queryParams),
                HttpMethod.GET,
                getHeaders(),
                ArrayNode.class);
        return response.getBody();
    }

    public JsonNode getSymbol24HourTickerPriceChange(String symbol) throws UnsupportedEncodingException {
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("symbol", symbol + "USD");
        HttpEntity<ObjectNode> response = restTemplate.exchange(
                binanceConfiguration.getApiHost() + "api/v3/ticker/24hr?" + getQueryParamsString(queryParams),
                HttpMethod.GET,
                getHeaders(),
                ObjectNode.class);
        return response.getBody();
    }

    public double getSymbolTickerPrice(String symbol) throws UnsupportedEncodingException {
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("symbol", symbol + "USD");
        HttpEntity<ObjectNode> response = restTemplate.exchange(
                binanceConfiguration.getApiHost() + "api/v3/ticker/price?" + getQueryParamsString(queryParams),
                HttpMethod.GET,
                getHeaders(),
                ObjectNode.class);
        return response.getBody().get("price").asDouble();
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
        if (queryParams.size() == 0) {
            return uriBase;
        }
        String queryParamsString = getQueryParamsString(queryParams);
        String signature = Hashing.hmacSha256(binanceConfiguration.getApiSecret().getBytes(StandardCharsets.UTF_8))
                .hashString(queryParamsString, StandardCharsets.UTF_8).toString();
        return uriBase + "?" + queryParamsString + "&signature=" + signature;
    }

    private HttpEntity<?> getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.set("X-MBX-APIKEY", binanceConfiguration.getApiKey());
        return new HttpEntity<>(headers);
    }
}
