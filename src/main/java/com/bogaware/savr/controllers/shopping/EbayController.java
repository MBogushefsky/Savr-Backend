package com.bogaware.savr.controllers.shopping;

import com.bogaware.savr.services.shopping.EbayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;

@Controller
@RequestMapping("/ebay")
public class EbayController {

    @Autowired
    private EbayService ebayService;

    @GetMapping("products")
    @ResponseBody
    public String getProductsByKeyword(@RequestParam(name = "keyword") String keyword) throws UnsupportedEncodingException {
        return ebayService.getProductsByKeyword(keyword);
    }
}
