package com.bogaware.savr.services;

import com.bogaware.savr.configurations.TwilioConfiguration;
import com.bogaware.savr.models.*;
import com.bogaware.savr.repositories.PlaidTokenRepository;
import com.bogaware.savr.repositories.PlaidTransactionRepository;
import com.bogaware.savr.repositories.UserPreferenceRepository;
import com.bogaware.savr.repositories.UserRepository;
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
public class GoodMorningService {

    private TwilioService twilioService;
    private TwilioConfiguration twilioConfiguration;
    private UserRepository userRepository;
    private UserPreferenceRepository userPreferenceRepository;

    @Autowired
    public GoodMorningService(TwilioService twilioService,
                              TwilioConfiguration twilioConfiguration,
                              UserRepository userRepository,
                              UserPreferenceRepository userPreferenceRepository) {
        this.twilioService = twilioService;
        this.twilioConfiguration = twilioConfiguration;
        this.userRepository = userRepository;
        this.userPreferenceRepository = userPreferenceRepository;
    }

    @Async
    @Scheduled(cron = "0 0 16 ? * *", zone = "UTC") //Every 15 minutes
    @Transactional
    public void execute() {
        System.out.println("Sending out Good Mornings...");
        List<User> users = userRepository.findAll();
        users = users.stream().filter(user -> {
            UserPreference userPreference = userPreferenceRepository.findByUserIdAndKey(user.getId(), "GoodMorning");
            if (userPreference == null) {
                return false;
            }
            return userPreference.isValue();
        }).collect(Collectors.toList());
        if (users.size() > 0) {
            for (User user: users) {
                TwilioMessage goodMorningTwilioMessage = new TwilioMessage(java.util.UUID.randomUUID().toString().toUpperCase(),
                        user.getPhoneNumber(),
                        twilioConfiguration.getPhoneNumber(),
                        false,
                        "Good Morning!",
                        new java.sql.Timestamp(new java.util.Date().getTime()));
                twilioService.sendMessage(goodMorningTwilioMessage);
            }
        }
        System.out.println("Sent All Good Mornings");
    }

}
