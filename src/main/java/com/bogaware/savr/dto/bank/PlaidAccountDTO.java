package com.bogaware.savr.dto.bank;

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
public class PlaidAccountDTO {
    @JsonProperty
    private String Id;
    @JsonProperty
    private String userId;
    @JsonProperty
    private String accountId;
    @JsonProperty
    private String institutionId;
    @JsonProperty
    private String name;
    @JsonProperty
    private String type;
    @JsonProperty
    private String subType;
    @JsonProperty
    private double availableBalance;
    @JsonProperty
    private double currentBalance;

}
