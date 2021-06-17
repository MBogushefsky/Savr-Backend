package com.bogaware.savr.enums.shopping;

import lombok.Getter;

@Getter
public enum SearchSortBy {
    PRICE_LOW_TO_HIGH("price_low_to_high"),
    PRICE_HIGH_TO_LOW("price_high_to_low"),
    REVIEW_SCORE("review_score");

    public final String label;

    private SearchSortBy(String label) {
        this.label = label;
    }
}
