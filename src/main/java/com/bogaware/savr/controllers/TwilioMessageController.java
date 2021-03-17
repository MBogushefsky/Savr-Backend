package com.bogaware.savr.controllers;

import com.bogaware.savr.services.TwilioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.io.UnsupportedEncodingException;

@Controller
@RequestMapping("/api/twilio")
public class TwilioMessageController {

    @Autowired
    TwilioService twilioService;

    @PostMapping("")
    @ResponseBody
    public void receivedMessage(@RequestBody String receivedMessage) throws UnsupportedEncodingException {
        twilioService.handleReceivedMessage(receivedMessage);
    }
}
