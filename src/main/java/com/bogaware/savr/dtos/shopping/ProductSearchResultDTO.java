package com.bogaware.savr.dtos.shopping;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonSerialize
@AllArgsConstructor
@NoArgsConstructor
public class ProductSearchResultDTO {
    @JsonProperty
    private String id;
    @JsonProperty
    private String title;
    @JsonProperty
    private String smallDescription;
    @JsonProperty
    private String link;
    @JsonProperty
    private double rating;
    @JsonProperty
    private String imageSrc;
    @JsonProperty
    private String merchant;
    @JsonProperty
    private double price;
}
