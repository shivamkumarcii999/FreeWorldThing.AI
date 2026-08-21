package com.cleartaxer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxCalculationRequest {
    @Builder.Default
    private AssessmentYear assessmentYear = AssessmentYear.AY_2024_25;

    @Builder.Default
    private AgeCategory ageCategory = AgeCategory.BELOW_60;

    @Builder.Default
    private TaxRegime preferredRegime = null; // null means calculate for both and compare

    @Builder.Default
    private IncomeDetails income = new IncomeDetails();

    @Builder.Default
    private DeductionDetails deductions = new DeductionDetails();
}
