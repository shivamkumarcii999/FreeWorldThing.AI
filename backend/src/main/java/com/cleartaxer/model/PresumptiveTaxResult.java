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
public class PresumptiveTaxResult {
    private PresumptiveSection section;
    private AssessmentYear assessmentYear;

    private double totalGrossReceipts;
    private double digitalReceipts;
    private double cashReceipts;
    private double digitalPercentage;

    /** Presumptive Scheme Threshold for the year (e.g. 75L for 44ADA digital, 3Cr for 44AD digital) */
    private double eligibilityThreshold;
    private boolean isEligible;
    private String eligibilityReason;

    /** Deemed Net Profit Rate & Amount */
    private double deemedProfitRate;
    private double deemedProfitAmount;

    /** Total Taxable Income including other income */
    private double totalGrossIncome;

    /** Regime Comparison for the Presumptive filer */
    private TaxCalculationResult oldRegimeCalculation;
    private TaxCalculationResult newRegimeCalculation;
    private TaxRegime recommendedRegime;
    private double taxPayable;

    /** Advance Tax Installment Schedule */
    @Builder.Default
    private List<AdvanceTaxInstallment> advanceTaxSchedule = new ArrayList<>();

    /** GST Threshold Evaluation */
    private double gstThreshold;
    private boolean gstRegistrationMandatory;
    private String gstApplicabilityNote;
}
