package com.bogaware.savr.dtos.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;

@Getter
@Setter
@JsonSerialize
@AllArgsConstructor
@NoArgsConstructor
public class ChangeUserPreferenceDTO {
    @JsonProperty
    private String typeId;
    @JsonProperty
    private String userId;
    @JsonProperty
    private Time preferredTime;
    @JsonProperty
    private String value;
}
