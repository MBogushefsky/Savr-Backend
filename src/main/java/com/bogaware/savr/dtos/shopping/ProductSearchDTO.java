package com.bogaware.savr.dtos.shopping;

import com.bogaware.savr.enums.shopping.ProductCondition;
import com.bogaware.savr.enums.shopping.SearchSortBy;
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
public class ProductSearchDTO {
    @JsonProperty
    private String query;
    @JsonProperty
    private SearchSortBy sortBy;
    @JsonProperty
    private String location;
    @JsonProperty
    private ProductCondition condition;
    @JsonProperty
    private int page;
    @JsonProperty
    private int pageSize;
    @JsonProperty
    private double minimumPrice;
    @JsonProperty
    private double maximumPrice;
}
