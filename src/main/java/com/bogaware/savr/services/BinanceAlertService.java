package com.bogaware.savr.services;

import com.bogaware.savr.configurations.BinanceAlertConfiguration;
import com.bogaware.savr.configurations.BinanceConfiguration;
import com.bogaware.savr.models.BinanceBalance;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class BinanceAlertService {

    private BinanceAlertConfiguration binanceAlertConfiguration;
    private BinanceService binanceService;
    private TwilioService twilioService;

    private HashMap<String, Double> previousPriceMap = new HashMap<>();

    @Autowired
    public BinanceAlertService(BinanceAlertConfiguration binanceAlertConfiguration,
                               BinanceService binanceService,
                               TwilioService twilioService) throws UnsupportedEncodingException {
        this.binanceAlertConfiguration = binanceAlertConfiguration;
        this.binanceService = binanceService;
        this.twilioService = twilioService;
        for (BinanceBalance binanceBalance : binanceService.getAccountBalances()) {
            if (!binanceBalance.getAssetName().equalsIgnoreCase("USD")) {
                double tickerPrice = binanceService.getSymbolTickerPrice(binanceBalance.getAssetName());
                previousPriceMap.put(binanceBalance.getAssetName(), tickerPrice);
            }
        }
    }

    @Async
    @Scheduled(cron = "${alert.frequentCron}", zone = "UTC") // Every 15 minutes during working hours
    public void alertOfSignificantPriceChangeOfPortfolio() throws UnsupportedEncodingException {
        System.out.println("Checking for Cryptocurrency Significant Change...");
        List<BinanceBalance> binanceBalances = binanceService.getAccountBalances();
        String resultMessage = "";
        for (BinanceBalance binanceBalance : binanceBalances) {
            if (!binanceBalance.getAssetName().equalsIgnoreCase("USD")) {
                double tickerPrice = binanceService.getSymbolTickerPrice(binanceBalance.getAssetName());
                if (previousPriceMap.containsKey(binanceBalance.getAssetName())) {
                    double previousPrice = previousPriceMap.get(binanceBalance.getAssetName());
                    double percentChange = ((tickerPrice - previousPrice) / previousPrice) * 100;
                    System.out.println(binanceBalance.getAssetName() + " price change: " + percentChange);
                    if (Math.abs(percentChange) > binanceAlertConfiguration.getSignificantPriceChangePercent()) {
                        resultMessage += binanceBalance.getAssetName() + " moved " + percentChange;
                    }
                }
                previousPriceMap.put(binanceBalance.getAssetName(), tickerPrice);
            }
        }
        if (resultMessage.length() != 0) {
            twilioService.sendMessage(twilioService.twilioMessageBuilder("14808885436", resultMessage));
        }
        System.out.println("Cryptocurrency Significant Change Checked");
    }

}
