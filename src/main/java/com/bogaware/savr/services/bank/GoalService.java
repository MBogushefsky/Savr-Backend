package com.bogaware.savr.services.bank;

import com.bogaware.savr.dto.bank.GoalDTO;
import com.bogaware.savr.dto.bank.GoalTypeDTO;
import com.bogaware.savr.dto.bank.PlaidAccountDTO;
import com.bogaware.savr.dto.bank.PlaidTransactionDTO;
import com.bogaware.savr.models.bank.Goal;
import com.bogaware.savr.models.bank.GoalType;
import com.bogaware.savr.models.bank.GoalValue;
import com.bogaware.savr.repositories.bank.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GoalService {

    private GoalRepository goalRepository;
    private GoalValueRepository goalValueRepository;
    private GoalTypeRepository goalTypeRepository;

    private PlaidAccountService plaidAccountService;
    private PlaidTransactionService plaidTransactionService;

    private ObjectMapper objectMapper;
    private DateFormat formatter;

    @Autowired
    public GoalService(GoalRepository goalRepository,
                       GoalValueRepository goalValueRepository,
                       GoalTypeRepository goalTypeRepository,
                       PlaidAccountService plaidAccountService,
                       PlaidTransactionService plaidTransactionService) {
        this.goalRepository = goalRepository;
        this.goalValueRepository = goalValueRepository;
        this.goalTypeRepository = goalTypeRepository;
        this.plaidAccountService = plaidAccountService;
        this.plaidTransactionService = plaidTransactionService;
        this.objectMapper = new ObjectMapper();
        this.formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        this.formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Async
    @Scheduled(cron = "${alert.dayStartCron}", zone = "UTC") // Every 15 minutes during working hours
    @Transactional
    protected void syncAll() throws Exception {
        System.out.println("Syncing All Goal Values...");
        List<Goal> allGoals = goalRepository.findAll();
        for (Goal goal : allGoals) {
            ArrayList<GoalValue> goalValuesToSave = new ArrayList<>();
            goalValuesToSave.addAll(addCalculatedGoalValues(goal, null, null));
            goalValueRepository.saveAll(goalValuesToSave);
        }
        System.out.println("All Goal Values Synced");
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

    public GoalDTO getGoalById(String userId, String goalId) {
        Goal goal = goalRepository.findByGoalIdAndUserId(goalId, userId);
        List<GoalValue> goalValues = goalValueRepository.findAllByGoalId(goal.getId());
        HashMap<String, Object> goalValuesMap = new HashMap<>();
        for (GoalValue goalValue : goalValues) {
            goalValuesMap.put(goalValue.getName(), goalValue.getValue());
        }
        GoalDTO goalDTO = new GoalDTO(goal.getId(), goal.getTypeId(), goal.getName(), goalValuesMap);
        return goalDTO;
    }

    public void editGoal(String userId, GoalDTO goal) {
        Goal foundGoal = goalRepository.findByGoalIdAndUserId(goal.getId(), userId);
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
        Goal foundGoal = goalRepository.findByGoalIdAndUserId(goal.getId(), userId);
        goalValuesToSave.addAll(addCalculatedGoalValues(foundGoal, goalValuesToSave, null));
        goalValueRepository.saveAll(goalValuesToSave);
    }

    public void deleteGoal(String userId, String goalId) {
        Goal foundGoal = goalRepository.findByGoalIdAndUserId(goalId, userId);
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

    private ArrayList<GoalValue> addCalculatedGoalValues(Goal goal, List<GoalValue> goalValues, List<GoalType> allGoalTypes) {
        ArrayList<GoalValue> resultGoalValues = new ArrayList<>();
        if (goalValues == null) {
            goalValues = goalValueRepository.findAllByGoalId(goal.getId());
        }
        if (allGoalTypes == null) {
            allGoalTypes = goalTypeRepository.findAll();
        }
        HashMap<String, Object> goalValuesMap = new HashMap<>();
        for (GoalValue goalValue : goalValues) {
            goalValuesMap.put(goalValue.getName(), goalValue.getValue());
        }
        for (GoalType goalType : allGoalTypes) {
            if (goalType.getId().equalsIgnoreCase(goal.getTypeId())) {
                if (goalType.getName().equalsIgnoreCase("Category Budget")) {
                    goalValuesMap = addCategoryBudgetCalculatedValues(goal, goalValuesMap);
                }
                else if (goalType.getName().equalsIgnoreCase("Savings")) {
                    goalValuesMap = addSavingsCalculatedValues(goal, goalValuesMap);
                }
                else if (goalType.getName().equalsIgnoreCase("Debt Payoff")) {
                    goalValuesMap = addDebtPayoffCalculatedValues(goal, goalValuesMap);
                }
            }
        }
        resultGoalValues = (ArrayList) goalValues;
        for (Map.Entry<String, Object> entry : goalValuesMap.entrySet()) {
            boolean foundGoalValue = false;
            for (GoalValue goalValue : resultGoalValues) {
                if (goalValue.getName().equalsIgnoreCase(entry.getKey())) {
                    goalValue.setValue(entry.getValue().toString());
                    foundGoalValue = true;
                }
            }
            if (!foundGoalValue) {
                resultGoalValues.add(new GoalValue(java.util.UUID.randomUUID().toString().toUpperCase(), goal.getId(),
                            entry.getKey(), entry.getValue().toString()));
            }
        }
        return resultGoalValues;
    }

    private HashMap<String, Object> addCategoryBudgetCalculatedValues(Goal goal, HashMap<String, Object> goalValuesMap) {
        String timeframe = goalValuesMap.get("timeframe").toString();
        try {
            Date endDate = goalValuesMap.get("endDate") != null ?
                    formatter.parse(goalValuesMap.get("endDate").toString()) : null;
            Date currentDate = new Date();
            if (endDate == null || currentDate.getTime() > endDate.getTime()) {
                endDate = endDate != null ? endDate : new Date();
                if (timeframe.equalsIgnoreCase("1 Week")) {
                    endDate.setDate(endDate.getDate() + 7);
                }
                else if (timeframe.equalsIgnoreCase("2 Weeks")) {
                    endDate.setDate(endDate.getDate() + 14);
                }
                else if (timeframe.equalsIgnoreCase("1 Month")) {
                    endDate.setMonth(endDate.getMonth() + 1);
                }
                else if (timeframe.equalsIgnoreCase("2 Months")) {
                    endDate.setMonth(endDate.getMonth() + 2);
                }
                else if (timeframe.equalsIgnoreCase("3 Months")) {
                    endDate.setMonth(endDate.getMonth() + 3);
                }
                goalValuesMap.put("endDate", formatter.format(endDate));
            }
            List<PlaidTransactionDTO> plaidTransactions = plaidTransactionService.findAllByUserIdInTimeRange(goal.getUserId(),
                    new java.sql.Date(formatter.parse(goalValuesMap.get("startDate").toString()).getTime()),
                    new java.sql.Date(formatter.parse(goalValuesMap.get("endDate").toString()).getTime()));
            Double currentSpent = 0.0;
            for (PlaidTransactionDTO plaidTransaction : plaidTransactions) {
                if (plaidTransaction.getCategories().contains(goalValuesMap.get("category")) && plaidTransaction.getAmount() < 0) {
                    currentSpent += Math.abs(plaidTransaction.getAmount());
                }
            }
            goalValuesMap.put("currentSpent", currentSpent);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        goalValuesMap.put("lastUpdated", formatter.format(new Date()));
        return goalValuesMap;
    }

    private HashMap<String, Object> addSavingsCalculatedValues(Goal goal, HashMap<String, Object> goalValuesMap) {
        PlaidAccountDTO plaidAccount = plaidAccountService.getAccountsById(goalValuesMap.get("account").toString());
        if (!goalValuesMap.containsKey("startingBalance")) {
            goalValuesMap.put("startingBalance", plaidAccount.getAvailableBalance());
        }
        if (goalValuesMap.containsKey("currentBalance")) {
            goalValuesMap.put("currentBalance", plaidAccount.getAvailableBalance());
        }
        else {
            goalValuesMap.put("currentBalance", goalValuesMap.get("startingBalance"));
        }
        goalValuesMap.put("saved", Double.parseDouble(goalValuesMap.get("currentBalance").toString()) -
                Double.parseDouble(goalValuesMap.get("startingBalance").toString()));
        goalValuesMap.put("lastUpdated", formatter.format(new Date()));
        return goalValuesMap;
    }

    private HashMap<String, Object> addDebtPayoffCalculatedValues(Goal goal, HashMap<String, Object> goalValuesMap) {
        return goalValuesMap;
    }
}
