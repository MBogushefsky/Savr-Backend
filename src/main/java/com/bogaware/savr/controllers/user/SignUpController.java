package com.bogaware.savr.controllers.user;

import com.bogaware.savr.dtos.user.UserDTO;
import com.bogaware.savr.models.user.User;
import com.bogaware.savr.repositories.user.UserRepository;
import com.bogaware.savr.services.user.UserSignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/sign-up")
public class SignUpController {

    @Autowired
    UserSignUpService userSignUpService;

    @GetMapping("username-available")
    @ResponseBody
    public boolean checkIfUsernameIsAvailable(@RequestParam(name = "request-username") String requestUsername) {
        if (requestUsername.trim().isEmpty()) { return false; }
        return !userSignUpService.doesUsernameExist(requestUsername);
    }

    /*@PutMapping("")
    @ResponseBody
    public boolean signUp(@RequestBody UserDTO userDTO) {
        if (userDTO.getPasswordHash().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot have any empty passwords");
        }
        userDTO.setId(java.util.UUID.randomUUID().toString().toUpperCase());
        User userToSignUp = new User();
        userToSignUp.setDTO(userDTO);
        userRepository.save(userToSignUp);
        return true;
    }*/
}