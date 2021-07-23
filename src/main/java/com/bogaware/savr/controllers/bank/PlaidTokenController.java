package com.bogaware.savr.controllers.bank;

import com.bogaware.savr.models.bank.PlaidToken;
import com.bogaware.savr.repositories.bank.PlaidTokenRepository;
import com.bogaware.savr.services.bank.PlaidService;
import com.bogaware.savr.services.user.UserAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/tokens")
public class PlaidTokenController {

    @Autowired
    UserAuthorizationService userAuthorizationService;

    @Autowired
    PlaidService plaidService;

    @Autowired
    PlaidTokenRepository plaidTokenRepository;

    @GetMapping("link/create")
    @ResponseBody
    public String getLinkToken(@RequestHeader("Authorization") String userId) {
        if (userAuthorizationService.hasAccess(userId)) {
            return plaidService.getLinkToken(userId);
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }

    @PostMapping("link")
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
