package com.bogaware.savr.enums.shopping;

import lombok.Getter;

@Getter
public enum ProductCondition {
    NEW("new"),
    USED("used");

    public final String label;

    private ProductCondition(String label) {
        this.label = label;
    }
}
