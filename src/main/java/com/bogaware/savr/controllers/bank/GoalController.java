package com.bogaware.savr.controllers.bank;

import com.bogaware.savr.dtos.bank.GoalDTO;
import com.bogaware.savr.dtos.bank.GoalTypeDTO;
import com.bogaware.savr.models.user.User;
import com.bogaware.savr.repositories.user.UserRepository;
import com.bogaware.savr.services.bank.GoalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequestMapping("/goals")
public class GoalController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoalService goalService;

    ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("")
    @ResponseBody
    public List<GoalDTO> getGoals(@RequestHeader("Authorization") String userId) {
        User currentUser = userRepository.findById(userId).get();
        if (currentUser != null) {
            return goalService.getAllGoals(userId);
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }

    @GetMapping("{id}")
    @ResponseBody
    public GoalDTO getGoalById(@RequestHeader("Authorization") String userId,
                               @PathVariable("id") String id) {
        User currentUser = userRepository.findById(userId).get();
        if (currentUser != null) {
            return goalService.getGoalById(userId, id);
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }

    @GetMapping("types")
    @ResponseBody
    public List<GoalTypeDTO> getGoalTypes(@RequestHeader("Authorization") String userId) {
        User currentUser = userRepository.findById(userId).get();
        if (currentUser != null) {
            return goalService.getGoalTypes();
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }

    @PutMapping("{id}")
    @ResponseBody
    public void editGoal(@RequestHeader("Authorization") String userId,
                         @PathVariable("id") String id,
                         @RequestBody GoalDTO goal) {
        User currentUser = userRepository.findById(userId).get();
        if (currentUser != null) {
            goal.setId(id);
            goalService.editGoal(userId, goal);
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }

    @PostMapping("")
    @ResponseBody
    public void addGoal(@RequestHeader("Authorization") String userId,
                        @RequestBody GoalDTO goal) {
        User currentUser = userRepository.findById(userId).get();
        if (currentUser != null) {
            goalService.addGoal(userId, goal);
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }

    @DeleteMapping("{id}")
    @ResponseBody
    public void deleteGoal(@RequestHeader("Authorization") String userId,
                           @PathVariable("id") String id) {
        User currentUser = userRepository.findById(userId).get();
        if (currentUser != null) {
            goalService.deleteGoal(userId, id);
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }
}
