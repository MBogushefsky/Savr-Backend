package com.bogaware.savr.controllers.bank;

import com.bogaware.savr.models.bank.PlaidAccount;
import com.bogaware.savr.models.bank.PlaidTransaction;
import com.bogaware.savr.models.user.User;
import com.bogaware.savr.repositories.bank.PlaidAccountRepository;
import com.bogaware.savr.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.util.List;

@Controller
@RequestMapping("/accounts")
public class PlaidAccountController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlaidAccountRepository plaidAccountRepository;

    @GetMapping("")
    @ResponseBody
    public List<PlaidAccount> getAccounts(@RequestHeader("Authorization") String userId) {
        User currentUser = userRepository.findById(userId).get();
        if (currentUser != null) {
            return plaidAccountRepository.findAllByUserId(currentUser.getId());
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }

    @GetMapping("{id}")
    @ResponseBody
    public PlaidAccount getAccountById(@RequestHeader("Authorization") String userId, @PathVariable("id") String id) {
        User currentUser = userRepository.findById(userId).get();
        if (currentUser != null) {
            return plaidAccountRepository.findByAccountId(id);
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }
}
