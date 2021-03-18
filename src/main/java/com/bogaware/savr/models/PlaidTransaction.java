package com.bogaware.savr.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
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
    @Column(name = "Amount")
    private double amount;
    @Column(name = "MerchantName")
    private String merchantName;
    @Column(name = "Name")
    private String name;
    @Column(name = "Date")
    private Date date;

    public void setContents(PlaidTransaction plaidTransaction) {
        this.setUserId(plaidTransaction.getUserId());
        this.setAccountId(plaidTransaction.getAccountId());
        this.setAmount(plaidTransaction.getAmount());
        this.setMerchantName(plaidTransaction.getMerchantName());
        this.setName(plaidTransaction.getName());
        this.setDate(plaidTransaction.getDate());
    }
}
