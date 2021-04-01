package com.bogaware.savr.services;

import com.bogaware.savr.dto.PlaidTransactionDTO;
import com.bogaware.savr.models.PlaidToken;
import com.bogaware.savr.models.PlaidTransaction;
import com.bogaware.savr.models.User;
import com.bogaware.savr.repositories.PlaidTokenRepository;
import com.bogaware.savr.repositories.PlaidTransactionRepository;
import com.bogaware.savr.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plaid.client.response.TransactionsGetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
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
        return plaidTransactionRepository.findAllByAccountId(accountId).stream()
                .map(plaidTransaction -> {
                    try {
                        return new PlaidTransactionDTO(plaidTransaction);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).collect(Collectors.toList());
    }

    public List<PlaidTransactionDTO> findAllByUserId(String userId) {
        return plaidTransactionRepository.findAllByUserId(userId).stream()
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
