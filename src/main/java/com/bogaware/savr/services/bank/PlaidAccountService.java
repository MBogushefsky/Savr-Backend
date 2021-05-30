package com.bogaware.savr.services.bank;

import com.bogaware.savr.dto.bank.GoalDTO;
import com.bogaware.savr.dto.bank.GoalTypeDTO;
import com.bogaware.savr.dto.bank.PlaidAccountDTO;
import com.bogaware.savr.dto.bank.PlaidTransactionDTO;
import com.bogaware.savr.models.bank.*;
import com.bogaware.savr.repositories.bank.GoalRepository;
import com.bogaware.savr.repositories.bank.GoalTypeRepository;
import com.bogaware.savr.repositories.bank.GoalValueRepository;
import com.bogaware.savr.repositories.bank.PlaidAccountRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlaidAccountService {

    private PlaidAccountRepository plaidAccountRepository;

    private ObjectMapper objectMapper;

    @Autowired
    public PlaidAccountService(PlaidAccountRepository plaidAccountRepository) {
        this.plaidAccountRepository = plaidAccountRepository;
        this.objectMapper = new ObjectMapper();
    }

    public List<PlaidAccountDTO> getAllAccounts(String userId) {
        return convertAccountsToDTOs(plaidAccountRepository.findAllByUserId(userId));
    }

    public PlaidAccountDTO getAccountsById(String accountId) {
        return convertAccountToDTO(plaidAccountRepository.findByAccountId(accountId));
    }

    public PlaidAccountDTO getAccountsByIdAndUserId(String userId, String accountId) {
        return convertAccountToDTO(plaidAccountRepository.findByAccountIdAndUserId(accountId, userId));
    }

    public PlaidAccountDTO convertAccountToDTO(PlaidAccount account) {
        return new PlaidAccountDTO(account.getId(), account.getUserId(),
                account.getAccountId(), account.getInstitutionId(),
                account.getName(), account.getType(), account.getSubType(),
                account.getAvailableBalance(), account.getCurrentBalance());
    }

    public List<PlaidAccountDTO> convertAccountsToDTOs(List<PlaidAccount> accounts) {
        return accounts.stream()
                .map(account -> {
                    return new PlaidAccountDTO(account.getId(), account.getUserId(),
                            account.getAccountId(), account.getInstitutionId(),
                            account.getName(), account.getType(), account.getSubType(),
                            account.getAvailableBalance(), account.getCurrentBalance());
                }).collect(Collectors.toList());
    }
}
