package com.cleartaxer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomeDetails {
    /** Gross Salary before Standard Deduction */
    @Builder.Default
    private double grossSalary = 0.0;

    /** Income from Self-Occupied or Let-Out House Property (Negative for Home Loan Interest Loss) */
    @Builder.Default
    private double incomeFromHouseProperty = 0.0;

    /** Business / Professional Income (Non-presumptive) */
    @Builder.Default
    private double businessIncome = 0.0;

    /** Short Term Capital Gains */
    @Builder.Default
    private double shortTermCapitalGains = 0.0;

    /** Long Term Capital Gains */
    @Builder.Default
    private double longTermCapitalGains = 0.0;

    /** Savings Account Interest, FD Interest, Dividend, etc. */
    @Builder.Default
    private double incomeFromOtherSources = 0.0;

    /** Optional detailed HRA information */
    private HraDetails hraDetails;

    public double getTotalGrossIncome() {
        return grossSalary + incomeFromHouseProperty + businessIncome +
               shortTermCapitalGains + longTermCapitalGains + incomeFromOtherSources;
    }
}
