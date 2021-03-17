package com.bogaware.savr.services;

import com.bogaware.savr.models.PlaidToken;
import com.bogaware.savr.models.PlaidTransaction;
import com.bogaware.savr.models.User;
import com.bogaware.savr.repositories.PlaidTokenRepository;
import com.bogaware.savr.repositories.PlaidTransactionRepository;
import com.bogaware.savr.repositories.UserRepository;
import com.plaid.client.response.TransactionsGetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlaidTransactionSyncService {

    private PlaidService plaidService;
    private PlaidTransactionRepository plaidTransactionRepository;
    private PlaidTokenRepository plaidTokenRepository;
    private TwilioService twilioService;
    private UserRepository userRepository;

    @Autowired
    public PlaidTransactionSyncService(PlaidService plaidService,
                                       PlaidTransactionRepository plaidTransactionRepository,
                                       PlaidTokenRepository plaidTokenRepository,
                                       TwilioService twilioService,
                                       UserRepository userRepository) {
        this.plaidService = plaidService;
        this.plaidTransactionRepository = plaidTransactionRepository;
        this.plaidTokenRepository = plaidTokenRepository;
        this.twilioService = twilioService;
        this.userRepository = userRepository;
    }

    @Async
    @Scheduled(fixedDelay = 900000, initialDelay = 900000) //Every 15 minutes
    @Transactional
    public void syncAll() throws Exception {
        System.out.println("Syncing All Transactions...");
        List<PlaidToken> plaidTokens = plaidTokenRepository.findAll();
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DAY_OF_MONTH, -2);
        Calendar endDate = Calendar.getInstance();
        for (PlaidToken plaidToken: plaidTokens) {
            List<TransactionsGetResponse.Transaction> transactions = plaidService.getTransactions(plaidToken.getAccessToken(), startDate, endDate).getTransactions();
            List<PlaidTransaction> plaidTransactions = transactions.stream().map(transaction ->
                    new PlaidTransaction(java.util.UUID.randomUUID().toString().toUpperCase(),
                            plaidToken.getUserId(),
                            transaction.getAccountId(),
                            transaction.getTransactionId(),
                            transaction.getAmount() * -1,
                            transaction.getMerchantName(),
                            transaction.getName(),
                            Date.valueOf(transaction.getDate()))
            ).collect(Collectors.toList());
            analysis(plaidToken.getUserId(), plaidTransactions);
            saveOrUpdate(plaidTransactions);
        }
        System.out.println("All Transactions Synced");
    }

    public void analysis(String userId, List<PlaidTransaction> updatedPlaidTransactions) {
        List<PlaidTransaction> oldPlaidTransactions = plaidTransactionRepository.findAllByUserId(userId);
        List<PlaidTransaction> newPlaidTransactions = updatedPlaidTransactions.stream()
                .filter(plaidTransaction -> {
                    boolean foundTransaction = false;
                    for (PlaidTransaction oldPlaidTransaction: oldPlaidTransactions) {
                        if (plaidTransaction.getTransactionId().equals(oldPlaidTransaction.getTransactionId())) {
                            foundTransaction = true;
                        }
                    }
                    return !foundTransaction;
                }).collect(Collectors.toList());
        if (newPlaidTransactions.size() > 0) {
            User userToSendMessage = userRepository.findById(userId).get();
            twilioService.sendNewTransactionsUpdate(userToSendMessage.getPhoneNumber(), newPlaidTransactions);
        }
    }

    @Transactional
    public void saveOrUpdate(List<PlaidTransaction> plaidTransactions) {
        for (PlaidTransaction plaidTransaction: plaidTransactions) {
            PlaidTransaction foundPlaidTransaction = plaidTransactionRepository
                    .findByTransactionId(plaidTransaction.getTransactionId());
            if (foundPlaidTransaction != null) {
                foundPlaidTransaction.setContents(plaidTransaction);
                plaidTransactionRepository.save(foundPlaidTransaction);
            }
            else {
                plaidTransactionRepository.save(plaidTransaction);
            }
        }
    }

}
