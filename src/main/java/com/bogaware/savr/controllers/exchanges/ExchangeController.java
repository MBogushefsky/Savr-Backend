package com.bogaware.savr.controllers.exchanges;

import com.bogaware.savr.dto.exchanges.SymbolSearchResultDTO;
import com.bogaware.savr.services.exchanges.BinanceService;
import com.bogaware.savr.services.exchanges.MarketStackService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.util.ArrayList;

@Controller
@RequestMapping("/exchanges")
public class ExchangeController {

    @Autowired
    BinanceService binanceService;

    @Autowired
    private MarketStackService marketStackService;

    @GetMapping("search")
    @ResponseBody
    public ArrayList<SymbolSearchResultDTO> searchSymbol(@RequestParam("query") String query) throws UnsupportedEncodingException {
        ArrayList<SymbolSearchResultDTO> searchResultDTOs = new ArrayList<>();
        ArrayNode stockSearchResults = marketStackService.searchForSymbol(query);
        for (JsonNode stockSearchResult : stockSearchResults) {
            searchResultDTOs.add(new SymbolSearchResultDTO(stockSearchResult.get("name").asText(),
                    stockSearchResult.get("symbol").asText(), true));
        }
        return searchResultDTOs;
    }

    @GetMapping("{symbol}/metadata")
    @ResponseBody
    public ObjectNode getSymbolMetadata(@PathVariable("symbol") String symbol,
                                         @RequestParam("is-stock") boolean isStock) throws UnsupportedEncodingException {
        if (isStock) {
            return marketStackService.getTickerData(symbol, false, false, false);
        }
        return null;
    }

    @GetMapping("{symbol}/price")
    @ResponseBody
    public JsonNode getSymbolPrice(@PathVariable("symbol") String symbol,
                                   @RequestParam("is-stock") boolean isStock) throws UnsupportedEncodingException {
        if (isStock) {
            return marketStackService.getIntradayOfSymbol(symbol, "1min", null,  null,1)
                    .withArray("data").get(0);
        }
        return null;
    }

    @GetMapping("{symbol}/price-history")
    @ResponseBody
    public ObjectNode getSymbolPriceHistory(@PathVariable("symbol") String symbol,
                                         @RequestParam("is-stock") boolean isStock,
                                         @RequestParam("interval") String interval,
                                         @RequestParam(value = "start-date", required = false) Date startDate,
                                         @RequestParam(value = "end-date", required = false) Date endDate) throws UnsupportedEncodingException {
        if (isStock) {
            return marketStackService.getIntradayOfSymbol(symbol, interval, startDate, endDate, 100);
        }
        return null;
    }
}
