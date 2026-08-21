package com.cleartaxer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegimeComparisonResult {
    private AssessmentYear assessmentYear;
    private TaxCalculationResult oldRegime;
    private TaxCalculationResult newRegime;

    /** Recommended Regime (OLD or NEW) */
    private TaxRegime recommendedRegime;

    /** Amount saved by choosing recommended regime */
    private double taxSaved;

    /** Human-readable rationale for the recommendation */
    private String recommendationSummary;

    /** Deduction optimization suggestions (e.g. Unused 80C, 80D, 80CCD(1B) headroom) */
    private List<String> optimizationTips;

    /** Key metrics comparison map */
    private Map<String, Object> comparisonMetrics;
}
