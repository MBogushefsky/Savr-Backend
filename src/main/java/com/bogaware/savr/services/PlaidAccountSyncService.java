package com.bogaware.savr.services;

import com.bogaware.savr.models.PlaidAccount;
import com.bogaware.savr.models.PlaidToken;
import com.bogaware.savr.models.PlaidTransaction;
import com.bogaware.savr.repositories.PlaidAccountRepository;
import com.bogaware.savr.repositories.PlaidTokenRepository;
import com.plaid.client.response.Account;
import com.plaid.client.response.AccountsGetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaidAccountSyncService {

    private PlaidService plaidService;
    private PlaidAccountRepository plaidAccountRepository;
    private PlaidTokenRepository plaidTokenRepository;

    @Autowired
    public PlaidAccountSyncService(PlaidService plaidService,
                                   PlaidAccountRepository plaidAccountRepository,
                                   PlaidTokenRepository plaidTokenRepository) {
        this.plaidService = plaidService;
        this.plaidAccountRepository = plaidAccountRepository;
        this.plaidTokenRepository = plaidTokenRepository;
    }

    @Async
    @Scheduled(fixedDelay = 900000, initialDelay = 900000) //Every 15 minutes
    @Transactional
    public void syncAll() {
        System.out.println("Syncing All Accounts...");
        List<PlaidToken> plaidTokens = plaidTokenRepository.findAll();
        for (PlaidToken plaidToken: plaidTokens) {
            List<Account> accounts = plaidService.getAccounts(plaidToken.getAccessToken()).getAccounts();
            List<PlaidAccount> plaidAccounts = accounts.stream().map(account ->
                    new PlaidAccount(java.util.UUID.randomUUID().toString().toUpperCase(),
                            plaidToken.getUserId(),
                            account.getAccountId(),
                            account.getOfficialName() != null ? account.getOfficialName() : account.getName(),
                            account.getType(),
                            account.getSubtype(),
                            account.getBalances().getAvailable(),
                            account.getBalances().getCurrent())
            ).collect(Collectors.toList());
            saveOrUpdate(plaidAccounts);
        }
        System.out.println("All Accounts Synced");
    }

    @Transactional
    public void saveOrUpdate(List<PlaidAccount> plaidAccounts) {
        for (PlaidAccount plaidAccount: plaidAccounts) {
            PlaidAccount foundPlaidTransaction = plaidAccountRepository
                    .findByAccountId(plaidAccount.getAccountId());
            if (foundPlaidTransaction != null) {
                foundPlaidTransaction.setContents(plaidAccount);
                plaidAccountRepository.save(foundPlaidTransaction);
            }
            else {
                plaidAccountRepository.save(plaidAccount);
            }
        }
    }
}
