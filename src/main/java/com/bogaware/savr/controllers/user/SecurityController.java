package com.bogaware.savr.controllers.user;

import com.bogaware.savr.dto.user.ChangePasswordDTO;
import com.bogaware.savr.models.user.User;
import com.bogaware.savr.repositories.user.UserRepository;
import com.bogaware.savr.services.user.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/security")
public class SecurityController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    SecurityService securityService;

    @PutMapping("password")
    @ResponseBody
    public boolean changePassword(@RequestHeader("Authorization") String userId, @RequestBody ChangePasswordDTO changePasswordDTO) {
        User currentUser = userRepository.findById(userId).get();
        if (currentUser != null) {
            String currentPasswordToSet = changePasswordDTO.getCurrentPassword();
            String newPasswordToSet = changePasswordDTO.getNewPassword();
            String newPasswordConfirmToSet = changePasswordDTO.getNewPasswordConfirm();
            if (currentPasswordToSet.isEmpty() || newPasswordToSet.isEmpty() || newPasswordConfirmToSet.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot have any empty passwords");
            }

            if (!newPasswordToSet.equals(newPasswordConfirmToSet)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
            }

            if (currentUser.getPasswordHash().equals(currentPasswordToSet)) {
                currentUser.setPasswordHash(newPasswordToSet);
                userRepository.save(currentUser);
                return true;
            }
            else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrent current password");
            }
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }
}