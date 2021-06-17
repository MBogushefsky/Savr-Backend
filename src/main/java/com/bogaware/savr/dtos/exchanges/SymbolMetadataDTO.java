package com.bogaware.savr.dtos.exchanges;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonSerialize
@AllArgsConstructor
public class SymbolMetadataDTO {
    @JsonProperty
    private final String name;
    @JsonProperty
    private final String symbol;
    @JsonProperty
    private final boolean isStock;
}
