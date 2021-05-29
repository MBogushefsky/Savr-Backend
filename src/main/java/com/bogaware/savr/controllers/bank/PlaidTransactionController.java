package com.bogaware.savr.controllers.bank;

import com.bogaware.savr.dto.bank.PlaidTransactionDTO;
import com.bogaware.savr.models.user.User;
import com.bogaware.savr.repositories.bank.PlaidTokenRepository;
import com.bogaware.savr.repositories.bank.PlaidTransactionRepository;
import com.bogaware.savr.repositories.user.UserRepository;
import com.bogaware.savr.services.bank.PlaidTransactionService;
import com.bogaware.savr.services.bank.PlaidTransactionSyncService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.util.*;

@Controller
@RequestMapping("/transactions")
public class PlaidTransactionController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlaidTokenRepository plaidTokenRepository;

    @Autowired
    private PlaidTransactionService plaidTransactionService;

    @Autowired
    private PlaidTransactionRepository plaidTransactionRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    PlaidTransactionSyncService plaidTransactionSyncService;

    @GetMapping("")
    @ResponseBody
    public List<PlaidTransactionDTO> getTransactions(@RequestHeader("Authorization") String userId,
                                                     @RequestParam("accountIds") List<String> accountIds,
                                                     @RequestParam("query") String query,
                                                     @RequestParam(value = "start-date", required = false)
                                                                             Date startDate,
                                                     @RequestParam(value = "end-date", required = false)
                                                                             Date endDate) {
        User currentUser = userRepository.findById(userId).get();
        if (currentUser != null) {
            if ((accountIds == null || accountIds.size() == 0) && query == null && startDate == null && endDate == null) {
                return plaidTransactionService.findAllByUserId(userId);
            }
            else {
                return plaidTransactionService.searchAllByAccountIdInTimeRange(accountIds, query, startDate, endDate);
            }
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }

    @GetMapping("ungrouped")
    @ResponseBody
    public List<PlaidTransactionDTO> getTransactionsByAccountIdsInTimeRangeUngrouped(@RequestHeader("Authorization") String userId,
                                                                                    @RequestParam("accountIds") List<String> accountIds,
                                                                                     @RequestParam(value = "start-date", required = false)
                                                                                                          Date startDate,
                                                                                     @RequestParam(value = "end-date", required = false)
                                                                                                          Date endDate) {
        User currentUser = userRepository.findById(userId).get();
        if (currentUser != null) {
            List<PlaidTransactionDTO> ungroupedTransactions = plaidTransactionService.findAllByAccountIdInTimeRangeUngrouped(accountIds, startDate, endDate);
            return ungroupedTransactions;
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }

    @GetMapping("grouped")
    @ResponseBody
    public ArrayList<ArrayList<PlaidTransactionDTO>> getTransactionsByAccountIdsInTimeRangeGrouped(@RequestHeader("Authorization") String userId,
                                                                                                  @RequestParam("accountIds") List<String> accountIds,
                                                                                                  @RequestParam(value = "start-date", required = false)
                                                                                                          Date startDate,
                                                                                                  @RequestParam(value = "end-date", required = false)
                                                                                                          Date endDate) {
        User currentUser = userRepository.findById(userId).get();
        if (currentUser != null) {
            ArrayList<ArrayList<PlaidTransactionDTO>> groupedTransactions = plaidTransactionService.findAllByAccountIdInTimeRangeGrouped(accountIds, startDate, endDate);
            return groupedTransactions;
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }

    @GetMapping("categories")
    @ResponseBody
    public ObjectNode getCategoriesWithNetAmount(@RequestHeader("Authorization") String userId,
                                                 @RequestParam("accountIds") List<String> accountIds,
                                                 @RequestParam(value = "start-date", required = false) Date startDate,
                                                 @RequestParam(value = "end-date", required = false) Date endDate) {
        User currentUser = userRepository.findById(userId).get();
        if (currentUser != null) {
            List<PlaidTransactionDTO> ungroupedTransactions = plaidTransactionService.findAllByAccountIdInTimeRangeUngrouped(accountIds, startDate, endDate);
            ObjectNode categoriesWithNetAmount = objectMapper.createObjectNode();
            for (PlaidTransactionDTO transactionDTO : ungroupedTransactions) {
                String categoryOfTransaction = transactionDTO.getCategories().size() > 0 ?
                        transactionDTO.getCategories().get(0) : null;
                if (categoriesWithNetAmount.get(categoryOfTransaction) != null) {
                    categoriesWithNetAmount.put(categoryOfTransaction,
                            categoriesWithNetAmount.get(categoryOfTransaction).asDouble() + transactionDTO.getAmount());
                }
                else {
                    categoriesWithNetAmount.put(categoryOfTransaction, transactionDTO.getAmount());
                }
            }
            return categoriesWithNetAmount;
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
