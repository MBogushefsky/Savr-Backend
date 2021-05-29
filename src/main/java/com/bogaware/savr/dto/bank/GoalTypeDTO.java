package com.bogaware.savr.dto.bank;

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
public class GoalTypeDTO {
    @JsonProperty
    private String Id;
    @JsonProperty
    private String name;
}
