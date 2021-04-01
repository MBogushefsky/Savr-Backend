package com.bogaware.savr.controllers;

import com.bogaware.savr.dto.PlaidTransactionDTO;
import com.bogaware.savr.models.PlaidToken;
import com.bogaware.savr.models.PlaidTransaction;
import com.bogaware.savr.models.User;
import com.bogaware.savr.repositories.PlaidAccountRepository;
import com.bogaware.savr.repositories.PlaidTokenRepository;
import com.bogaware.savr.repositories.PlaidTransactionRepository;
import com.bogaware.savr.repositories.UserRepository;
import com.bogaware.savr.services.PlaidAccountSyncService;
import com.bogaware.savr.services.PlaidService;
import com.bogaware.savr.services.PlaidTransactionService;
import com.bogaware.savr.services.PlaidTransactionSyncService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/transactions")
public class PlaidTransactionController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlaidTokenRepository plaidTokenRepository;

    @Autowired
    private PlaidTransactionService plaidTransactionService;

    @Autowired
    private PlaidTransactionRepository plaidTransactionRepository;

    @Autowired
    PlaidTransactionSyncService plaidTransactionSyncService;

    @GetMapping("{accountId}")
    @ResponseBody
    public List<PlaidTransactionDTO> getTransactionsByAccountId(@RequestHeader("Authorization") String userId,
                                                                @PathVariable("accountId") String accountId) {
        User currentUser = userRepository.findById(userId).get();
        if (currentUser != null) {
            return plaidTransactionService.findAllByAccountId(accountId);
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }

    @GetMapping("")
    @ResponseBody
    public List<PlaidTransactionDTO> getAllTransactions(@RequestHeader("Authorization") String userId) {
        User currentUser = userRepository.findById(userId).get();
        if (currentUser != null) {
            return plaidTransactionService.findAllByUserId(userId);
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }

    @DeleteMapping("{accountId}")
    @ResponseBody
    public void hardRefreshTransactionsByPlaidAccountId(@RequestHeader("Authorization") String userId,
                                                      @PathVariable("accountId") String accountId) throws Exception {
        User currentUser = userRepository.findById(userId).get();
        if (currentUser != null) {
            plaidTransactionRepository.deleteAllByAccountId(accountId);
            plaidTransactionSyncService.syncAllWithOptions(false);
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }
}
