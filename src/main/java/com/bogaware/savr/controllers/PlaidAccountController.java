package com.bogaware.savr.controllers;

import com.bogaware.savr.models.PlaidAccount;
import com.bogaware.savr.models.PlaidToken;
import com.bogaware.savr.models.User;
import com.bogaware.savr.repositories.PlaidAccountRepository;
import com.bogaware.savr.repositories.PlaidTokenRepository;
import com.bogaware.savr.repositories.UserRepository;
import com.bogaware.savr.services.PlaidService;
import com.bogaware.savr.services.UserAuthorizationService;
import com.plaid.client.response.AccountsBalanceGetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/accounts")
public class PlaidAccountController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlaidTokenRepository plaidTokenRepository;

    @Autowired
    PlaidAccountRepository plaidAccountRepository;

    @GetMapping("")
    @ResponseBody
    public List<PlaidAccount> getAccounts(@RequestHeader("Authorization") String userId) {
        User currentUser = userRepository.findById(userId).get();
        if (currentUser != null) {
            return plaidAccountRepository.findAllByUserId(currentUser.getId());
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }
}
