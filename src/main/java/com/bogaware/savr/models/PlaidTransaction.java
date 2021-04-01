package com.bogaware.savr.models;

import com.bogaware.savr.dto.PlaidTransactionDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "plaid_transaction")
public class PlaidTransaction {
    @Column(name = "ID")
    @Id
    private String Id;
    @Column(name = "UserID")
    private String userId;
    @Column(name = "AccountID")
    private String accountId;
    @Column(name = "TransactionID")
    private String transactionId;
    @Column(name = "MerchantName")
    private String merchantName;
    @Column(name = "Name")
    private String name;
    @Column(name = "Categories")
    private String categories;
    @Column(name = "Amount")
    private double amount;
    @Column(name = "Date")
    private Date date;

    public void setContents(PlaidTransaction plaidTransaction) {
        this.setUserId(plaidTransaction.getUserId());
        this.setAccountId(plaidTransaction.getAccountId());
        this.setMerchantName(plaidTransaction.getMerchantName());
        this.setName(plaidTransaction.getName());
        this.setAmount(plaidTransaction.getAmount());
        this.setCategories(plaidTransaction.getCategories());
        this.setDate(plaidTransaction.getDate());
    }
}
