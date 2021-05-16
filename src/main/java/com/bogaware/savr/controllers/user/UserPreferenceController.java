package com.bogaware.savr.controllers.user;

import com.bogaware.savr.dto.user.ChangeUserPreferenceDTO;
import com.bogaware.savr.models.user.User;
import com.bogaware.savr.models.user.UserPreference;
import com.bogaware.savr.models.user.UserPreferenceType;
import com.bogaware.savr.repositories.user.UserPreferenceRepository;
import com.bogaware.savr.repositories.user.UserPreferenceTypeRepository;
import com.bogaware.savr.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequestMapping("/user-preferences")
public class UserPreferenceController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserPreferenceTypeRepository userPreferenceRepositoryType;

    @Autowired
    UserPreferenceRepository userPreferenceRepository;

    @GetMapping("")
    @ResponseBody
    public List<UserPreference> getUserPreferencesByUserId(@RequestHeader("Authorization") String userId) {
        User currentUser = userRepository.findById(userId).get();
        if (currentUser != null) {
            return this.userPreferenceRepository.findAllByUserId(userId);
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }

    @GetMapping("types")
    @ResponseBody
    public List<UserPreferenceType> getAllPreferenceTypes(@RequestHeader("Authorization") String userId) {
        User currentUser = userRepository.findById(userId).get();
        if (currentUser != null) {
            return this.userPreferenceRepositoryType.findAll();
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }

    @PutMapping("{typeId}")
    @ResponseBody
    public UserPreference setUserPreference(@RequestHeader("Authorization") String userId,
                                     @PathVariable("typeId") String typeId,
                                     @RequestBody ChangeUserPreferenceDTO changeUserPreferenceDTO) {
        User currentUser = userRepository.findById(userId).get();
        if (currentUser != null) {
            UserPreference foundUserPreference = this.userPreferenceRepository.findByUserIdAndType(userId, typeId);
            if (foundUserPreference != null) {
                foundUserPreference.setValue(changeUserPreferenceDTO.getValue());
                return this.userPreferenceRepository.save(foundUserPreference);
            }
            else {
                UserPreference userPreferenceToAdd = new UserPreference(typeId, userId, changeUserPreferenceDTO.getValue());
                return this.userPreferenceRepository.save(userPreferenceToAdd);
            }
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }

    @PutMapping("")
    @ResponseBody
    public boolean setAllUserPreferences(@RequestHeader("Authorization") String userId,
                                     @RequestBody List<ChangeUserPreferenceDTO> changeUserPreferenceDTOs) {
        User currentUser = userRepository.findById(userId).get();
        if (currentUser != null) {
            for (ChangeUserPreferenceDTO changeUserPreferenceDTO : changeUserPreferenceDTOs) {
                UserPreference foundUserPreference = this.userPreferenceRepository.findByUserIdAndType(userId, changeUserPreferenceDTO.getTypeId());
                if (foundUserPreference != null) {
                    foundUserPreference.setValue(changeUserPreferenceDTO.getValue());
                    this.userPreferenceRepository.save(foundUserPreference);
                }
                else {
                    UserPreference userPreferenceToAdd = new UserPreference(changeUserPreferenceDTO.getTypeId(), userId, changeUserPreferenceDTO.getValue());
                    this.userPreferenceRepository.save(userPreferenceToAdd);
                }
            }
            return true;
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }
}