package com.bogaware.savr.dtos.bank;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;

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
