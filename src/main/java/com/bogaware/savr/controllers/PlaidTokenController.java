package com.bogaware.savr.controllers;

import com.bogaware.savr.models.PlaidToken;
import com.bogaware.savr.repositories.PlaidTokenRepository;
import com.bogaware.savr.services.PlaidService;
import com.bogaware.savr.services.UserAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/api/tokens")
public class PlaidTokenController {

    @Autowired
    UserAuthorizationService userAuthorizationService;

    @Autowired
    PlaidService plaidService;

    @Autowired
    PlaidTokenRepository plaidTokenRepository;

    @PostMapping("")
    @ResponseBody
    public void savePlaidToken(@RequestHeader("Authorization") String userId, @RequestParam(name = "token") String token) {
        if (userAuthorizationService.hasAccess(userId)) {
            String accessToken = plaidService.getAccessToken(token);
            plaidTokenRepository.save(new PlaidToken(java.util.UUID.randomUUID().toString().toUpperCase(), userId, accessToken));
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }
}
