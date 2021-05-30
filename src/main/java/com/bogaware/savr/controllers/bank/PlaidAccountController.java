package com.bogaware.savr.controllers.bank;

import com.bogaware.savr.dto.bank.PlaidAccountDTO;
import com.bogaware.savr.models.user.User;
import com.bogaware.savr.repositories.user.UserRepository;
import com.bogaware.savr.services.bank.PlaidAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequestMapping("/accounts")
public class PlaidAccountController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlaidAccountService plaidAccountService;

    @GetMapping("")
    @ResponseBody
    public List<PlaidAccountDTO> getAccounts(@RequestHeader("Authorization") String userId) {
        User currentUser = userRepository.findById(userId).get();
        if (currentUser != null) {
            return plaidAccountService.getAllAccounts(currentUser.getId());
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }

    @GetMapping("{id}")
    @ResponseBody
    public PlaidAccountDTO getAccountById(@RequestHeader("Authorization") String userId, @PathVariable("id") String id) {
        User currentUser = userRepository.findById(userId).get();
        if (currentUser != null) {
            return plaidAccountService.getAccountsByIdAndUserId(userId, id);
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }
}
