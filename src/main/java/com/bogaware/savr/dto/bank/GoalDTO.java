package com.bogaware.savr.dto.bank;

import com.bogaware.savr.models.bank.PlaidTransaction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jdk.nashorn.internal.ir.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@JsonSerialize
@AllArgsConstructor
@NoArgsConstructor
public class GoalDTO {
    @JsonProperty
    private String Id;
    @JsonProperty
    private String typeId;
    @JsonProperty
    private String name;
    @JsonProperty
    private HashMap<String, Object> values;
}
