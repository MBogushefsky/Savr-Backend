package com.bogaware.savr.controllers;

import com.bogaware.savr.models.PlaidToken;
import com.bogaware.savr.models.PlaidTransaction;
import com.bogaware.savr.models.User;
import com.bogaware.savr.repositories.PlaidAccountRepository;
import com.bogaware.savr.repositories.PlaidTokenRepository;
import com.bogaware.savr.repositories.PlaidTransactionRepository;
import com.bogaware.savr.repositories.UserRepository;
import com.bogaware.savr.services.PlaidService;
import com.plaid.client.response.TransactionsGetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Controller
@RequestMapping("/api/transactions")
public class PlaidTransactionController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlaidTokenRepository plaidTokenRepository;

    @Autowired
    private PlaidTransactionRepository plaidTransactionRepository;

    @GetMapping("{accountId}")
    @ResponseBody
    public List<PlaidTransaction> getTransactionsByAccountId(@RequestHeader("Authorization") String userId,
                                                             @PathVariable("accountId") String accountId) {
        User currentUser = userRepository.findById(userId).get();
        if (currentUser != null) {
            return plaidTransactionRepository.findAllByAccountId(accountId);
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }

    @GetMapping("all")
    @ResponseBody
    public List<PlaidTransaction> getAllTransactions(@RequestHeader("Authorization") String userId) {
        User currentUser = userRepository.findById(userId).get();
        if (currentUser != null) {
            return plaidTransactionRepository.findAllByUserId(userId);
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }

    @DeleteMapping("all")
    @ResponseBody
    public void deleteAllTransactions(@RequestHeader("Authorization") String userId) {
        User currentUser = userRepository.findById(userId).get();
        if (currentUser != null) {
            plaidTransactionRepository.deleteAll();
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }
}
