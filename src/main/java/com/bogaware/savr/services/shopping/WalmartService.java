package com.bogaware.savr.services.shopping;

import com.bogaware.savr.configurations.shopping.WalmartConfiguration;
import com.bogaware.savr.configurations.shopping.WebScrapingConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class WalmartService {

    @Autowired
    private WalmartConfiguration walmartConfiguration;

    @Autowired
    private WebScrapingConfiguration webScrapingConfiguration;

    private ObjectMapper objectMapper = new ObjectMapper();

    public String getUrl(String keyword, int page) throws UnsupportedEncodingException {
        String url = walmartConfiguration.getUrl() + "/search/?grid=true&query=" + URLEncoder.encode(keyword.trim(), StandardCharsets.UTF_8.toString());
        if (page != 1) {
            url += "&page=" + page;
        }
        return url;
    }

    public List<ObjectNode> getProductsByKeyword(String keyword, int page) throws UnsupportedEncodingException {
        List<ObjectNode> resultProducts = new ArrayList<>();
        String url = getUrl(keyword, page);
        try {
            Connection.Response response = Jsoup.connect(url)
                    .userAgent(webScrapingConfiguration.getUserAgent())
                    .referrer(webScrapingConfiguration.getReferrer())
                    //.ignoreContentType(webScrapingConfiguration.isIgnoreContentType())
                    //.followRedirects(webScrapingConfiguration.isFollowRedirects())
                    .timeout(webScrapingConfiguration.getTimeout())
                    .execute();
            if (!response.url().toString().equalsIgnoreCase(url)) {
                keyword += "_";
                url = getUrl(keyword, page);
                response = Jsoup.connect(url)
                        .userAgent(webScrapingConfiguration.getUserAgent())
                        .referrer(webScrapingConfiguration.getReferrer())
                        .ignoreContentType(webScrapingConfiguration.isIgnoreContentType())
                        .followRedirects(webScrapingConfiguration.isFollowRedirects())
                        .timeout(webScrapingConfiguration.getTimeout())
                        .execute();
            }
            Document document = response.parse();
            Elements searchResultElements = document.getElementsByAttribute("data-id");
            for (Element searchResult : searchResultElements) {
                try {
                    ObjectNode product = getProductEssentialsByElement(searchResult);
                    if (product != null) {
                        product.set("extras", getProductExtrasByElement(searchResult));
                        resultProducts.add(product);
                    }
                } catch (Exception e) {
                    System.out.println("Could not parse a product");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultProducts;
    }

    public ObjectNode getProductSpecsById(String productId) throws UnsupportedEncodingException {
        ObjectNode resultSpecs = objectMapper.createObjectNode();
        String url = walmartConfiguration.getUrl() + "/ip/" + URLEncoder.encode(productId, StandardCharsets.UTF_8.toString());
        try {
            Connection.Response response = Jsoup.connect(url)
                    .userAgent(webScrapingConfiguration.getUserAgent())
                    .referrer(webScrapingConfiguration.getReferrer())
                    .ignoreContentType(webScrapingConfiguration.isIgnoreContentType())
                    .followRedirects(webScrapingConfiguration.isFollowRedirects())
                    .timeout(webScrapingConfiguration.getTimeout())
                    .execute();
            Document document = response.parse();
            Elements widgetElements = document.getElementsByClass("product-specifications");
            for (Element widgetElement : widgetElements) {
                Elements tableElements = widgetElement.getElementsByTag("table");
                for (Element tableElement : tableElements) {
                    Elements trElementsOfTable = tableElement.getElementsByTag("tr");
                    for (Element trElementOfTable : trElementsOfTable) {
                        Elements tdElements  = trElementOfTable.getElementsByTag("td");
                        if (tdElements.size() == 2) {
                            resultSpecs.put(tdElements.first().text(), tdElements.last().text());
                        }
                    }
                }
            }
            if (resultSpecs.size() == 0) {
                Element comparisonArea = document.getElementsByClass("comparison").first();
                Elements comparisonTables = comparisonArea.getElementsByClass("comparison-table");
                List<String> keys = new ArrayList<>();
                List<String> values = new ArrayList<>();
                for (Element comparisonTable : comparisonTables) {
                    if (comparisonTable.getElementsByClass("comparison-key").size() > 0) {
                        Elements tdOfKeys = comparisonTable.getElementsByTag("td");
                        tdOfKeys.forEach(tdOfKey -> {
                            if (!tdOfKey.text().trim().equalsIgnoreCase("")) {
                                keys.add(tdOfKey.text());
                            }
                        });
                    }
                    else if (comparisonTable.getElementsByClass("comparison-root").size() > 0) {
                        Elements tdOfValues = comparisonTable.getElementsByTag("td");
                        tdOfValues.forEach(tdOfValue -> {
                            if (!tdOfValue.text().trim().equalsIgnoreCase("")) {
                                values.add(tdOfValue.text());
                            }
                        });
                    }
                }
                if (keys.size() == values.size()) {
                    for (int i = 0; i < keys.size(); i++) {
                        resultSpecs.put(keys.get(i), values.get(i));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultSpecs;
    }

    public ObjectNode getProductEssentialsByElement(Element productElement) {
        try {
            ObjectNode resultNode = objectMapper.createObjectNode();
            Element productImageAreaEle = productElement
                    .getElementsByClass("search-result-productimage")
                    .first();
            Element productImageEle = productImageAreaEle
                    .getElementsByTag("img")
                    .first();
            Elements priceAreaEles = productElement.getElementsByClass("price-main");
            String price;
            if (priceAreaEles.size() > 1) {
                String priceFrom = priceAreaEles.first()
                        .getElementsByClass("visuallyhidden").text();
                String priceTo = priceAreaEles.last()
                        .getElementsByClass("visuallyhidden").text();
                price = priceFrom + " to " + priceTo;
            }
            else {
                price = priceAreaEles.first()
                        .getElementsByClass("visuallyhidden").text();
            }
            resultNode.put("id", productElement.attr("data-id"));
            resultNode.put("title", productImageEle.attr("alt"));
            resultNode.put("link", walmartConfiguration.getUrl() + "/ip/" + resultNode.get("id").asText());
            resultNode.put("price", price);
            resultNode.put("imageSrc", productImageEle.attr("src"));
            return resultNode;
        }
        catch (Exception e) {
            return null;
        }
    }

    public ObjectNode getProductExtrasByElement(Element productElement) {
        try {
            ObjectNode resultNode = objectMapper.createObjectNode();
            resultNode.put("rating", Double.parseDouble(productElement
                    .getElementsByClass("seo-avg-rating").first().text()));
            resultNode.put("totalRating", Double.parseDouble(productElement
                    .getElementsByClass("seo-best-rating").first().text()));
            resultNode.put("walmartPlus", productElement.getElementsByClass("walmart-plus-message").size() > 0);
            return resultNode;
        }
        catch (Exception e) {
            return null;
        }
    }
}
