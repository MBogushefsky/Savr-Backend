package com.bogaware.savr.models.exchanges;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BinanceBalance {
    private String assetName;
    private double amount;
    private double equivalentInUSD;
}
