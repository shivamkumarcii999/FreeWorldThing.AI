package com.cleartaxer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxCalculationResult {
    private TaxRegime regime;
    private AssessmentYear assessmentYear;
    private AgeCategory ageCategory;
    private String ruleEngineVersion;

    /** Gross Income */
    private double grossTotalIncome;

    /** Standard Deduction (Sec 16(ia)) */
    private double standardDeduction;

    /** Total Chapter VI-A Deductions allowed */
    private double totalDeductionsAllowed;

    /** Total Net Taxable Income (Rounded to nearest 10 as per Sec 288A) */
    private double netTaxableIncome;

    /** Tax computed on slabs before rebate */
    private double taxOnSlabs;

    /** Slabs breakdown */
    @Builder.Default
    private List<SlabBreakdown> slabBreakdowns = new ArrayList<>();

    /** Section 87A Rebate */
    private double rebateSection87A;

    /** Tax after Rebate */
    private double taxAfterRebate;

    /** Surcharge Rate & Amount */
    private double surchargeRate;
    private double surchargeAmount;
    private double marginalReliefSurcharge;

    /** Health and Education Cess (4%) */
    private double healthAndEducationCess;

    /** Final Total Tax Liability (Rounded to nearest 10 as per Sec 288B) */
    private double totalTaxPayable;

    /** Effective Tax Rate (%) */
    private double effectiveTaxRate;

    /** Step-by-step verifiable calculation audit trace */
    @Builder.Default
    private List<AuditTraceStep> auditTrace = new ArrayList<>();
}
