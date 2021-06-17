package com.bogaware.savr.controllers.user;

import com.bogaware.savr.dtos.user.UserDTO;
import com.bogaware.savr.models.user.User;
import com.bogaware.savr.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("")
    @ResponseBody
    public UserDTO login(@RequestParam(name = "username") String username, @RequestParam(name = "passwordHash") String passwordHash) {
        User retrievedUser = userRepository.findByUsernameAndPasswordHash(username, passwordHash);
        if (retrievedUser != null) {
            return retrievedUser.getDTO();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }
}