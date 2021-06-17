package com.bogaware.savr.dtos.bank;

import com.bogaware.savr.models.bank.PlaidTransaction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.util.List;

@Getter
@Setter
@JsonSerialize
@AllArgsConstructor
@NoArgsConstructor
public class PlaidTransactionDTO {
    @JsonProperty
    private String Id;
    @JsonProperty
    private String userId;
    @JsonProperty
    private String accountId;
    @JsonProperty
    private String transactionId;
    @JsonProperty
    private String merchantName;
    @JsonProperty
    private String name;
    @JsonProperty
    private List<String> categories;
    @JsonProperty
    private double amount;
    @JsonProperty
    private Date date;

    @JsonIgnore
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PlaidTransactionDTO(PlaidTransaction plaidTransaction) throws JsonProcessingException {
        this.setId(plaidTransaction.getId());
        this.setUserId(plaidTransaction.getUserId());
        this.setAccountId(plaidTransaction.getAccountId());
        this.setTransactionId(plaidTransaction.getTransactionId());
        this.setMerchantName(plaidTransaction.getMerchantName());
        this.setName(plaidTransaction.getName());
        List<String> categoryList = objectMapper.readValue(plaidTransaction.getCategories(), new TypeReference<List<String>>(){});
        this.setCategories(categoryList);
        this.setAmount(plaidTransaction.getAmount());
        this.setDate(plaidTransaction.getDate());
    }

}
