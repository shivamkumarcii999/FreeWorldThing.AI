package com.cleartaxer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresumptiveTaxRequest {
    @Builder.Default
    private AssessmentYear assessmentYear = AssessmentYear.AY_2024_25;

    @Builder.Default
    private PresumptiveSection section = PresumptiveSection.SECTION_44ADA;

    /** Gross Receipts / Turnover received through digital modes (banking, UPI, NEFT, cards) */
    @Builder.Default
    private double digitalReceipts = 0.0;

    /** Gross Receipts / Turnover received in cash */
    @Builder.Default
    private double cashReceipts = 0.0;

    /** Declared Deemed Profit Percentage if user declares higher than minimum (e.g. 50% minimum for 44ADA) */
    @Builder.Default
    private Double customProfitPercentage = null;

    /** Other Income Sources (Interest, Dividends, etc.) */
    @Builder.Default
    private double otherIncome = 0.0;

    /** Deduction details for Old Regime if comparing */
    @Builder.Default
    private DeductionDetails deductions = new DeductionDetails();
}
