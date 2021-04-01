package com.bogaware.savr.services;

import com.bogaware.savr.repositories.UserPreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserPreferenceService {

    private UserPreferenceRepository userPreferenceRepository;

    @Autowired
    public UserPreferenceService(UserPreferenceRepository userPreferenceRepository) {
        this.userPreferenceRepository = userPreferenceRepository;
    }

    public boolean hasUserPreferenceByUserId(String userId, String userPreference) {
        return userPreferenceRepository.findByUserIdAndType(userId, userPreference) != null;
    }

}
