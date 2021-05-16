package com.bogaware.savr.controllers.user;

import com.bogaware.savr.services.user.TwilioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.io.UnsupportedEncodingException;

@Controller
@RequestMapping("/twilio")
public class TwilioMessageController {

    @Autowired
    TwilioService twilioService;

    @PostMapping("")
    @ResponseBody
    public void receivedMessage(@RequestBody String receivedMessage) throws UnsupportedEncodingException {
        twilioService.handleReceivedMessage(receivedMessage);
    }
}
