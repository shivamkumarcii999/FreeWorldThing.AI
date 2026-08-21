package com.cleartaxer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlabBreakdown {
    private String slabRange;
    private double minIncome;
    private double maxIncome;
    private double taxableAmountInSlab;
    private double ratePercentage;
    private double taxForSlab;
}
