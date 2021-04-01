package com.bogaware.savr.controllers;

import com.bogaware.savr.services.BlueCartService;
import com.bogaware.savr.services.EbayService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;

@Controller
@RequestMapping("/api/blue-cart")
public class BlueCartController {

    @Autowired
    private BlueCartService blueCartService;

    @GetMapping("products")
    @ResponseBody
    public ObjectNode getProductsByKeyword(@RequestParam(name = "keyword") String keyword) throws UnsupportedEncodingException {
        return blueCartService.getProductsByKeyword(keyword);
    }
}
