package com.bogaware.savr.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "plaid_account")
public class PlaidAccount {
    @Column(name = "ID")
    @Id
    private String Id;
    @Column(name = "UserID")
    private String userId;
    @Column(name = "AccountID")
    private String accountId;
    @Column(name = "Name")
    private String name;
    @Column(name = "Type")
    private String type;
    @Column(name = "SubType")
    private String subType;
    @Column(name = "AvailableBalance")
    private double availableBalance;
    @Column(name = "CurrentBalance")
    private double currentBalance;

    public void setContents(PlaidAccount plaidAccount) {
        this.setUserId(plaidAccount.getUserId());
        this.setAccountId(plaidAccount.getAccountId());
        this.setName(plaidAccount.getName());
        this.setType(plaidAccount.getType());
        this.setSubType(plaidAccount.getSubType());
        this.setAvailableBalance(plaidAccount.getAvailableBalance());
        this.setCurrentBalance(plaidAccount.getCurrentBalance());
    }
}
