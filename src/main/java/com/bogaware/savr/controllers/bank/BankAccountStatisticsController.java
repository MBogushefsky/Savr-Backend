package com.bogaware.savr.controllers.bank;

import com.bogaware.savr.dto.bank.PlaidTransactionDTO;
import com.bogaware.savr.models.user.User;
import com.bogaware.savr.repositories.bank.PlaidTokenRepository;
import com.bogaware.savr.repositories.bank.PlaidTransactionRepository;
import com.bogaware.savr.repositories.user.UserRepository;
import com.bogaware.savr.services.bank.PlaidTransactionService;
import com.bogaware.savr.services.bank.PlaidTransactionSyncService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/statistics")
public class BankAccountStatisticsController {

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
    public List<PlaidTransactionDTO> getAllTransactions(@RequestHeader("Authorization") String userId) {
        User currentUser = userRepository.findById(userId).get();
        if (currentUser != null) {
            return plaidTransactionService.findAllByUserId(userId);
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }

    @GetMapping("ungrouped")
    @ResponseBody
    public List<PlaidTransactionDTO> getTransactionsByAccountIdInTimeRangeUngrouped(@RequestHeader("Authorization") String userId,
                                                                                                  @RequestParam("accountIds") List<String> accountIds,
                                                                                                  @RequestParam(value = "startDate", required = false)
                                                                                                          Date startDate,
                                                                                                  @RequestParam(value = "endDate", required = false)
                                                                                                          Date endDate) {
        User currentUser = userRepository.findById(userId).get();
        if (currentUser != null) {
            List<PlaidTransactionDTO> groupedTransactions = plaidTransactionService.findAllByAccountIdInTimeRangeUngrouped(accountIds, startDate, endDate);
            return groupedTransactions;
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }

    @GetMapping("grouped")
    @ResponseBody
    public ArrayList<ArrayList<PlaidTransactionDTO>> getTransactionsByAccountIdInTimeRangeGrouped(@RequestHeader("Authorization") String userId,
                                                                                       @RequestParam("accountIds") List<String> accountIds,
                                                                                       @RequestParam(value = "startDate", required = false)
                                                                                               Date startDate,
                                                                                       @RequestParam(value = "endDate", required = false)
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
