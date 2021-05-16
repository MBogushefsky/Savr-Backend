package com.bogaware.savr.controllers.investing;

import com.bogaware.savr.services.investing.MarketStackService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@Controller
@RequestMapping("/investments")
public class MarketStackController {

    @Autowired
    private MarketStackService marketStackService;

    @GetMapping("stocks/{symbol}")
    @ResponseBody
    public ObjectNode getIntradayOfStock(@PathVariable("symbol") String symbol) throws UnsupportedEncodingException {
        return marketStackService.getIntradayOfSymbol(symbol);
    }
}
