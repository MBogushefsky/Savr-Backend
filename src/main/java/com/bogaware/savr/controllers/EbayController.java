package com.bogaware.savr.controllers;

import com.bogaware.savr.models.BinanceBalance;
import com.bogaware.savr.services.BinanceService;
import com.bogaware.savr.services.EbayService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller
@RequestMapping("/api/ebay")
public class EbayController {

    @Autowired
    private EbayService ebayService;

    @GetMapping("products")
    @ResponseBody
    public String getAccountBalances(@RequestParam(name = "keyword") String keyword) throws UnsupportedEncodingException {
        return ebayService.getProductsByKeyword(keyword);
    }
}
