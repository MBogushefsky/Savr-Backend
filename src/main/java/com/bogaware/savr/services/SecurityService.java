package com.bogaware.savr.services;

import com.bogaware.savr.repositories.UserPreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class SecurityService {

    private MessageDigest digest;

    @Autowired
    public SecurityService(UserPreferenceRepository userPreferenceRepository) throws NoSuchAlgorithmException {
        this.digest = MessageDigest.getInstance("SHA-256");
    }

    public String encrypt(String message) {
        String sha256hex = org.apache.commons.codec.digest.DigestUtils.sha256Hex(message);
        return sha256hex;
    }
}
