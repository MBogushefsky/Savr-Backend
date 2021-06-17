package com.bogaware.savr.services.user;

import com.bogaware.savr.models.user.User;
import com.bogaware.savr.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserSignUpService {

    @Autowired
    UserRepository userRepository;

    public boolean hasAccess(String userId) {
        return userRepository.findById(userId) != null;
    }

    public boolean doesUsernameExist(String username) { return userRepository.findByUsername(username) != null; }
}
