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


}
