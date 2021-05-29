package com.bogaware.savr.services.bank;

import com.bogaware.savr.dto.bank.GoalDTO;
import com.bogaware.savr.dto.bank.GoalTypeDTO;
import com.bogaware.savr.models.bank.Goal;
import com.bogaware.savr.models.bank.GoalType;
import com.bogaware.savr.models.bank.GoalValue;
import com.bogaware.savr.models.bank.PlaidAccount;
import com.bogaware.savr.repositories.bank.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GoalService {

    private GoalRepository goalRepository;
    private GoalValueRepository goalValueRepository;
    private GoalTypeRepository goalTypeRepository;

    private PlaidAccountRepository plaidAccountRepository;

    private ObjectMapper objectMapper;

    @Autowired
    public GoalService(GoalRepository goalRepository,
                       GoalValueRepository goalValueRepository,
                       GoalTypeRepository goalTypeRepository,
                       PlaidAccountRepository plaidAccountRepository) {
        this.goalRepository = goalRepository;
        this.goalValueRepository = goalValueRepository;
        this.goalTypeRepository = goalTypeRepository;
        this.plaidAccountRepository = plaidAccountRepository;
        this.objectMapper = new ObjectMapper();
    }

    public List<GoalDTO> getAllGoals(String userId) {
        ArrayList<GoalDTO> goalDTOs = new ArrayList<>();
        List<Goal> goals = goalRepository.findAllByUserId(userId);
        for (Goal goal : goals) {
            List<GoalValue> goalValues = goalValueRepository.findAllByGoalId(goal.getId());
            HashMap<String, Object> goalValuesMap = new HashMap<>();
            for (GoalValue goalValue : goalValues) {
                goalValuesMap.put(goalValue.getName(), goalValue.getValue());
            }
            goalDTOs.add(new GoalDTO(goal.getId(), goal.getTypeId(), goal.getName(), goalValuesMap));
        }
        return goalDTOs;
    }

    public void editGoal(String userId, GoalDTO goal) {
        Goal foundGoal = goalRepository.findAllByGoalIdAndUserId(goal.getId(), userId);
        foundGoal.setName(goal.getName());
        goalRepository.save(foundGoal);
        List<GoalValue> foundGoalValues = goalValueRepository.findAllByGoalId(foundGoal.getId());
        goalValueRepository.deleteAll(foundGoalValues);
        saveGoalValues(userId, goal);
    }

    public void addGoal(String userId, GoalDTO goal) {
        String goalId = java.util.UUID.randomUUID().toString().toUpperCase();
        Goal goalToSave = new Goal(goalId,
                userId, goal.getTypeId(), goal.getName(), new java.sql.Timestamp(new java.util.Date().getTime()));
        goalRepository.save(goalToSave);
        goal.setId(goalId);
        saveGoalValues(userId, goal);
    }

    public void saveGoalValues(String userId, GoalDTO goal) {
        ArrayList<GoalValue> goalValuesToSave = new ArrayList<>();
        for (Map.Entry<String, Object> entry : goal.getValues().entrySet()) {
            goalValuesToSave.add(new GoalValue(java.util.UUID.randomUUID().toString().toUpperCase(),
                    goal.getId(), entry.getKey(), entry.getValue().toString()));
        }
        goalValueRepository.saveAll(goalValuesToSave);
        Goal foundGoal = goalRepository.findAllByGoalIdAndUserId(goal.getId(), userId);
        addCalculatedGoalValues(foundGoal, goalValuesToSave, null);
    }

    public void deleteGoal(String userId, String goalId) {
        Goal foundGoal = goalRepository.findAllByGoalIdAndUserId(goalId, userId);
        if (foundGoal != null) {
            goalValueRepository.deleteAllByGoalId(goalId);
            goalRepository.deleteAllByIdAndUserId(goalId, userId);
        }
    }

    public List<GoalTypeDTO> getGoalTypes() {
        return goalTypeRepository.findAll().stream().map(goalType ->
                new GoalTypeDTO(goalType.getId(), goalType.getName()))
                .collect(Collectors.toList());
    }

    private void addCalculatedGoalValues(Goal goal, List<GoalValue> goalValues, List<GoalType> allGoalTypes) {
        if (goalValues == null) {
            goalValues = goalValueRepository.findAllByGoalId(goal.getId());
        }
        if (allGoalTypes == null) {
            allGoalTypes = goalTypeRepository.findAll();
        }
        for (GoalType goalType : allGoalTypes) {
            if (goalType.getId().equalsIgnoreCase(goal.getTypeId())) {
                if (goalType.getName().equalsIgnoreCase("Category Budget")) {
                    addCategoryBudgetCalculatedValues(goal, goalValues);
                }
                else if (goalType.getName().equalsIgnoreCase("Savings")) {
                    addSavingsCalculatedValues(goal, goalValues);
                }
                else if (goalType.getName().equalsIgnoreCase("Debt Payoff")) {
                    addDebtPayoffCalculatedValues(goal, goalValues);
                }
            }
        }
    }

    private void addCategoryBudgetCalculatedValues(Goal goal, List<GoalValue> goalValues) {

    }

    private void addSavingsCalculatedValues(Goal goal, List<GoalValue> goalValues) {
        HashMap<String, Object> goalValuesMap = new HashMap<>();
        for (GoalValue goalValue : goalValues) {
            goalValuesMap.put(goalValue.getName(), goalValue.getValue());
        }
        PlaidAccount plaidAccount = plaidAccountRepository.findByAccountId(goalValuesMap.get("account").toString());
        if (!goalValuesMap.containsKey("startingBalance")) {
            goalValuesMap.put("startingBalance", plaidAccount.getAvailableBalance());
        }
        if (goalValuesMap.containsKey("currentBalance")) {
            goalValuesMap.put("currentBalance", Double.parseDouble(goalValuesMap.get("currentBalance").toString()) -
                    Double.parseDouble(goalValuesMap.get("startingBalance").toString()));
        }
        else {
            goalValuesMap.put("currentBalance", goalValuesMap.get("startingBalance"));
        }
        goalValuesMap.put("saved", 0);
        goalValuesMap.put("lastUpdated", new java.sql.Timestamp(new java.util.Date().getTime()));
    }

    private void addDebtPayoffCalculatedValues(Goal goal, List<GoalValue> goalValues) {

    }
}
