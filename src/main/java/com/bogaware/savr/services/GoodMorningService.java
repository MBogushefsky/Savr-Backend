package com.bogaware.savr.services;

import com.bogaware.savr.configurations.TwilioConfiguration;
import com.bogaware.savr.models.*;
import com.bogaware.savr.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodMorningService {

    private TwilioService twilioService;
    private TwilioConfiguration twilioConfiguration;
    private UserRepository userRepository;
    private UserPreferenceService userPreferenceService;

    @Autowired
    public GoodMorningService(TwilioService twilioService,
                              TwilioConfiguration twilioConfiguration,
                              UserRepository userRepository,
                              UserPreferenceService userPreferenceService) {
        this.twilioService = twilioService;
        this.twilioConfiguration = twilioConfiguration;
        this.userRepository = userRepository;
        this.userPreferenceService = userPreferenceService;
    }

    @Async
    @Scheduled(cron = "${alert.dayStartCron}", zone = "UTC") // Day start
    @Transactional
    public void execute() {
        System.out.println("Sending out Good Mornings...");
        List<User> users = userRepository.findAll();
        users = users.stream().filter(user ->
                userPreferenceService.hasUserPreferenceByUserId(user.getId(), "GoodMorning"))
                .collect(Collectors.toList());
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
