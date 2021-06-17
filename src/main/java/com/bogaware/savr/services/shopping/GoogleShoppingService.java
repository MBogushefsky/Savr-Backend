package com.bogaware.savr.services.shopping;

import com.bogaware.savr.configurations.shopping.ValueSerpConfiguration;
import com.bogaware.savr.dtos.shopping.ProductSearchDTO;
import com.bogaware.savr.dtos.shopping.ProductSearchResultDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
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
import java.text.DecimalFormat;
import java.util.*;

@Service
public class GoogleShoppingService {

    private ValueSerpConfiguration valueSerpConfiguration;
    private RestTemplate restTemplate;

    private ObjectMapper objectMapper;
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");

    @Autowired
    public GoogleShoppingService(ValueSerpConfiguration valueSerpConfiguration,
                                 RestTemplate restTemplate,
                                 ObjectMapper objectMapper) {
        this.valueSerpConfiguration = valueSerpConfiguration;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public List<ProductSearchResultDTO> getSearchForProducts(ProductSearchDTO productSearchDTO) throws UnsupportedEncodingException, JsonProcessingException {
        HashMap<String, String> queryParams = getEssentialQueryParams();
        queryParams.put("q", productSearchDTO.getQuery());
        queryParams.put("shopping_condition", productSearchDTO.getCondition().getLabel());
        queryParams.put("location", productSearchDTO.getLocation());
        queryParams.put("sort_by", productSearchDTO.getSortBy().getLabel());
        queryParams.put("page", String.valueOf(productSearchDTO.getPage()));
        queryParams.put("num", String.valueOf(productSearchDTO.getPageSize()));
        HttpEntity<ObjectNode> response = restTemplate.exchange(
                valueSerpConfiguration.getApiUrl() + "/search?" + getQueryParamsString(queryParams),
                HttpMethod.GET,
                getHeaders(),
                ObjectNode.class);

        return getProductSearchResultsFromResponse(response.getBody());
    }

    private List<ProductSearchResultDTO> getProductSearchResultsFromResponse(ObjectNode response) {
        List<ProductSearchResultDTO> productSearchResultDTOList = new ArrayList<>();
        ArrayNode productResults = response.withArray("shopping_results");
        for (JsonNode productResult : productResults) {
            ProductSearchResultDTO result = new ProductSearchResultDTO();
            result.setId(productResult.get("id").asText());
            result.setTitle(productResult.get("title").asText());
            result.setSmallDescription(productResult.get("snippet").asText());
            result.setLink(productResult.get("link").asText());
            if (productResult.hasNonNull("rating")) {
                result.setRating(productResult.get("rating").asDouble());
            }
            result.setImageSrc(productResult.get("image").asText());
            result.setMerchant(productResult.get("merchant").asText());
            result.setPrice(productResult.get("price").asDouble());
            productSearchResultDTOList.add(result);
        }
        return productSearchResultDTOList;
    }

    private HashMap<String, String> getEssentialQueryParams() {
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("api_key", valueSerpConfiguration.getApiKey());
        queryParams.put("search_type", "shopping");
        queryParams.put("google_domain", "google.com");
        queryParams.put("gl", "us");
        queryParams.put("output", "json");
        queryParams.put("hl", "en");
        return queryParams;
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
