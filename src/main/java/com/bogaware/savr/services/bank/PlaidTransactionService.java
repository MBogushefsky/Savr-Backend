package com.bogaware.savr.services.bank;

import com.bogaware.savr.dto.bank.PlaidTransactionDTO;
import com.bogaware.savr.models.bank.PlaidTransaction;
import com.bogaware.savr.repositories.bank.PlaidTokenRepository;
import com.bogaware.savr.repositories.bank.PlaidTransactionRepository;
import com.bogaware.savr.repositories.user.UserRepository;
import com.bogaware.savr.services.user.TwilioService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlaidTransactionService {

    private PlaidService plaidService;
    private PlaidTransactionRepository plaidTransactionRepository;
    private PlaidTokenRepository plaidTokenRepository;
    private TwilioService twilioService;
    private UserRepository userRepository;

    private ObjectMapper objectMapper;

    @Autowired
    public PlaidTransactionService(PlaidService plaidService,
                                   PlaidTransactionRepository plaidTransactionRepository,
                                   PlaidTokenRepository plaidTokenRepository,
                                   TwilioService twilioService,
                                   UserRepository userRepository) {
        this.plaidService = plaidService;
        this.plaidTransactionRepository = plaidTransactionRepository;
        this.plaidTokenRepository = plaidTokenRepository;
        this.twilioService = twilioService;
        this.userRepository = userRepository;
        this.objectMapper = new ObjectMapper();
    }

    public List<PlaidTransactionDTO> findAllByAccountId(String accountId) {
        return convertTransactionsToDTOs(plaidTransactionRepository.findAllByAccountId(accountId));
    }

    public List<PlaidTransactionDTO> findAllByUserId(String userId) {
        return convertTransactionsToDTOs(plaidTransactionRepository.findAllByUserId(userId));
    }

    public ArrayList<ArrayList<PlaidTransactionDTO>> findAllByAccountIdInTimeRangeGrouped(List<String> accountIds, Date startDate,
                                                           Date endDate) {
        ArrayList<ArrayList<PlaidTransactionDTO>> resultList = new ArrayList<>();
        List<PlaidTransaction> transactions;
        if (startDate != null && endDate != null) {
            transactions = plaidTransactionRepository.findAllByAccountIdsInTimeRange(accountIds, startDate, endDate);
        }
        else {
            transactions = plaidTransactionRepository.findAllByAccountIds(accountIds);
        }
        List<PlaidTransactionDTO> transactionDTOs = convertTransactionsToDTOs(transactions);
        for (PlaidTransactionDTO transaction : transactionDTOs) {
            boolean foundGroup = false;
            for (ArrayList<PlaidTransactionDTO> inGroupTransactionList : resultList) {
                if (inGroupTransactionList.get(0).getDate().getTime() == transaction.getDate().getTime()) {
                    foundGroup = true;
                    inGroupTransactionList.add(transaction);
                }
            }
            if (!foundGroup) {
                resultList.add(new ArrayList(Arrays.asList(transaction)));
            }
        }
        return resultList;
    }

    public List<PlaidTransactionDTO> findAllByAccountIdInTimeRangeUngrouped(List<String> accountIds, Date startDate,
                                                                            Date endDate) {
        List<PlaidTransaction> transactions;
        if (startDate != null && endDate != null) {
            transactions = plaidTransactionRepository.findAllByAccountIdsInTimeRange(accountIds, startDate, endDate);
        }
        else {
            transactions = plaidTransactionRepository.findAllByAccountIds(accountIds);
        }
        List<PlaidTransactionDTO> transactionDTOs = convertTransactionsToDTOs(transactions);
        return transactionDTOs;
    }

    public List<PlaidTransactionDTO> findAllByUserIdInTimeRange(String userId, Date startDate,
                                                                     Date endDate) {
        List<PlaidTransaction> transactions;
        if (startDate != null && endDate != null) {
            transactions = plaidTransactionRepository.findAllByUserIdInTimeRange(userId, startDate, endDate);
        }
        else {
            transactions = plaidTransactionRepository.findAllByUserId(userId);
        }
        List<PlaidTransactionDTO> transactionDTOs = convertTransactionsToDTOs(transactions);
        return transactionDTOs;
    }

    public List<PlaidTransactionDTO> searchAllByAccountIdInTimeRange(List<String> accountIds, String query, Date startDate,
                                                                            Date endDate) {
        List<PlaidTransaction> transactions;
        if (startDate != null && endDate != null) {
            transactions = plaidTransactionRepository.searchAllByAccountIdsInTimeRange(accountIds, query, startDate, endDate);
        }
        else {
            transactions = plaidTransactionRepository.searchAllByAccountIds(accountIds, query);
        }
        List<PlaidTransactionDTO> transactionDTOs = convertTransactionsToDTOs(transactions);
        return transactionDTOs;
    }

    public List<PlaidTransactionDTO> convertTransactionsToDTOs(List<PlaidTransaction> transactions) {
        return transactions.stream()
                .map(plaidTransaction -> {
                    try {
                        return new PlaidTransactionDTO(plaidTransaction);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).collect(Collectors.toList());
    }
}
