package com.bogaware.savr.controllers.exchanges;

import com.bogaware.savr.services.exchanges.MarketStackService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Date;

@Controller
@RequestMapping("/investments")
public class MarketStackController {

    @Autowired
    private MarketStackService marketStackService;

    @GetMapping("stocks/{symbol}")
    @ResponseBody
    public ObjectNode getIntradayOfStock(@PathVariable("symbol") String symbol) throws UnsupportedEncodingException {
        //return marketStackService.getIntradayOfSymbol(symbol, "30min", new Date().toString(), new Date());
        return null;
    }
}
