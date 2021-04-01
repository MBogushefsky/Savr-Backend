package com.bogaware.savr.controllers;

import com.bogaware.savr.models.BinanceBalance;
import com.bogaware.savr.services.BinanceService;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller
@RequestMapping("/api/binance")
public class BinanceController {

    @Autowired
    BinanceService binanceService;

    @GetMapping("balances")
    @ResponseBody
    public List<BinanceBalance> getAccountBalances() throws UnsupportedEncodingException {
        return binanceService.getAccountBalances();
    }

    @GetMapping("candlestick/last-day/{symbol}")
    @ResponseBody
    public ArrayNode getCandlestickFor24Hours(@PathVariable("symbol") String symbol) throws UnsupportedEncodingException {
        return binanceService.getCandlestickFor24Hours(symbol);
    }
}
