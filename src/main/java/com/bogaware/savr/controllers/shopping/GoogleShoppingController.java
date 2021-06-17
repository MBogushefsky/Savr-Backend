package com.bogaware.savr.controllers.shopping;

import com.bogaware.savr.dtos.shopping.ProductSearchDTO;
import com.bogaware.savr.dtos.shopping.ProductSearchResultDTO;
import com.bogaware.savr.services.shopping.AmazonService;
import com.bogaware.savr.services.shopping.GoogleShoppingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("google-shopping")
@Slf4j
public class GoogleShoppingController {

    @Autowired
    private GoogleShoppingService googleShoppingService;

    @PutMapping("search-products")
    @ResponseBody
    public List<ProductSearchResultDTO> getProductsByKeyword(@RequestBody ProductSearchDTO productSearchDTO) throws UnsupportedEncodingException, JsonProcessingException {
        log.info("Calling for Google Shopping's Products with the keyword: " + productSearchDTO.getQuery() +
                ", on page: " + productSearchDTO.getPage());
        if (productSearchDTO.getQuery().trim().equalsIgnoreCase("")) {
            return new ArrayList<>();
        }
        return googleShoppingService.getSearchForProducts(productSearchDTO);
    }
}
