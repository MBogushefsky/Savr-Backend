package com.bogaware.savr.controllers;

import com.bogaware.savr.models.User;
import com.bogaware.savr.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/api/login")
public class LoginController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("")
    @ResponseBody
    public User login(@RequestParam(name = "username") String username, @RequestParam(name = "passwordHash") String passwordHash) {
        User retrievedUser = userRepository.findByUsernameAndPasswordHash(username, passwordHash);
        if (retrievedUser != null) {
            return retrievedUser;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }
}