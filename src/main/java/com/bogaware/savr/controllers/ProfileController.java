package com.bogaware.savr.controllers;

import com.bogaware.savr.dto.ProfileDTO;
import com.bogaware.savr.dto.UserDTO;
import com.bogaware.savr.models.User;
import com.bogaware.savr.repositories.UserRepository;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Controller
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    UserRepository userRepository;

    @PutMapping("")
    @ResponseBody
    public UserDTO changeProfileSettings(@RequestHeader("Authorization") String userId, @RequestBody ProfileDTO profileDTO) {
        User currentUser = userRepository.findById(userId).get();
        if (currentUser != null) {
            String firstNameToSet = profileDTO.getFirstName();
            String lastNameToSet = profileDTO.getLastName();
            String emailToSet = profileDTO.getEmail();
            if (firstNameToSet.isEmpty() || lastNameToSet.isEmpty() || emailToSet.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot have an empty profile field");
            }
            currentUser.setFirstName(firstNameToSet);
            currentUser.setLastName(lastNameToSet);
            currentUser.setEmail(emailToSet);

            User savedUser = userRepository.save(currentUser);
            return savedUser.getDTO();
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }

    @PostMapping("image")
    @ResponseBody
    public UserDTO uploadProfileImage(@RequestHeader("Authorization") String userId,
                                      @RequestParam("file") MultipartFile file) throws IOException {
        User currentUser = userRepository.findById(userId).get();
        if (currentUser != null) {
            currentUser.setProfileImage(file.getBytes());
            User savedUser = userRepository.save(currentUser);
            return savedUser.getDTO();
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }
}