package com.bogaware.savr.models.bank;

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
@Table(name = "plaid_token")
public class PlaidToken {
    @Column(name = "ID")
    @Id
    private String Id;
    @Column(name = "UserID")
    private String userId;
    @Column(name = "Access_Token")
    private String accessToken;
}
