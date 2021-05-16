package com.bogaware.savr.controllers.shopping;

import com.bogaware.savr.services.shopping.WalmartService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller
@RequestMapping("walmart")
@Slf4j
public class WalmartController {

    @Autowired
    private WalmartService walmartService;

    @GetMapping("products")
    @ResponseBody
    public List<ObjectNode> getProductsByKeyword(@RequestParam(name = "keyword") String keyword,
                                                 @RequestParam(name = "page") int page) throws UnsupportedEncodingException {
        return walmartService.getProductsByKeyword(keyword, page);
    }

    @GetMapping("products/specs/{product-id}")
    @ResponseBody
    public ObjectNode getProductSpecsById(@PathVariable("product-id") String productId) throws UnsupportedEncodingException {
        log.info("Calling for Walmart's Products Specs with ID: " + productId);
        if (productId == null || productId.trim().equalsIgnoreCase("")) {
            return null;
        }
        return walmartService.getProductSpecsById(productId);
    }
}
