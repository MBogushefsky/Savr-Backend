package com.bogaware.savr.services;

import com.bogaware.savr.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAuthorizationService {

    @Autowired
    UserRepository userRepository;

    public boolean hasAccess(String userId) {
        return userRepository.findById(userId) != null;
    }
}
