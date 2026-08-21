package com.cleartaxer.engine;

import com.cleartaxer.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AY2024_25_RuleEngine extends AbstractTaxRuleEngine {

    public static final String VERSION = "AY2024-25-v1.0.0-CBDT";
    public static final double STANDARD_DEDUCTION = 50000.0;

    private final DeductionCalculator deductionCalculator;

    @Override
    public AssessmentYear getAssessmentYear() {
        return AssessmentYear.AY_2024_25;
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public TaxCalculationResult calculateOldRegime(TaxCalculationRequest request) {
        IncomeDetails income = request.getIncome() != null ? request.getIncome() : new IncomeDetails();
        DeductionDetails deductions = request.getDeductions() != null ? request.getDeductions() : new DeductionDetails();
        AgeCategory ageCategory = request.getAgeCategory() != null ? request.getAgeCategory() : AgeCategory.BELOW_60;

        List<AuditTraceStep> trace = new ArrayList<>();
        int stepNum = 1;

        // 1. Gross Total Income
        double grossSalary = income.getGrossSalary();
        double housePropIncome = income.getIncomeFromHouseProperty();
        if (deductions.getHomeLoanInterestSelfOccupied() > 0) {
            // Section 24(b) deduction for self-occupied house property (capped at 2L)
            double allowedSec24b = Math.min(deductions.getHomeLoanInterestSelfOccupied(), DeductionCalculator.MAX_SEC_24B_LOSS);
            housePropIncome -= allowedSec24b;
            trace.add(AuditTraceStep.builder()
                    .stepNumber(stepNum++)
                    .stepName("Section 24(b) Home Loan Interest")
                    .ruleReference("Section 24(b)")
                    .description("Deduction for interest on housing loan for self-occupied property (capped at ₹2,00,000)")
                    .amount(-allowedSec24b)
                    .formattedDetails(String.format("Loss under House Property: -₹%,.0f", allowedSec24b))
                    .build());
        }

        double grossTotalIncome = grossSalary + housePropIncome + income.getBusinessIncome() +
                income.getShortTermCapitalGains() + income.getLongTermCapitalGains() + income.getIncomeFromOtherSources();

        trace.add(AuditTraceStep.builder()
                .stepNumber(stepNum++)
                .stepName("Gross Total Income")
                .ruleReference("Section 14")
                .description("Sum of all heads of income (Salary, House Property, Capital Gains, Business, Other Sources)")
                .amount(grossTotalIncome)
                .formattedDetails(String.format("Gross Income: ₹%,.0f", grossTotalIncome))
                .build());

        // 2. Standard Deduction for Salary
        double standardDeductionAllowed = (grossSalary > 0) ? Math.min(grossSalary, STANDARD_DEDUCTION) : 0.0;
        if (standardDeductionAllowed > 0) {
            trace.add(AuditTraceStep.builder()
                    .stepNumber(stepNum++)
                    .stepName("Standard Deduction")
                    .ruleReference("Section 16(ia)")
                    .description("Flat standard deduction for salaried individuals")
                    .amount(standardDeductionAllowed)
                    .formattedDetails(String.format("Standard Deduction: ₹%,.0f", standardDeductionAllowed))
                    .build());
        }

        // 3. Chapter VI-A Deductions
        DeductionCalculator.DeductionCalculationResult deductionResult = deductionCalculator.calculateOldRegimeDeductions(
                deductions, income.getHraDetails(), ageCategory, grossSalary, income.getIncomeFromOtherSources());
        double totalDeductions = deductionResult.totalDeductions();
        trace.addAll(deductionResult.auditSteps());

        // 4. Net Taxable Income
        double incomeAfterStd = Math.max(0, grossTotalIncome - standardDeductionAllowed);
        double netTaxableIncome = Math.max(0, incomeAfterStd - totalDeductions);
        netTaxableIncome = roundToNearestTen(netTaxableIncome);

        trace.add(AuditTraceStep.builder()
                .stepNumber(stepNum++)
                .stepName("Net Taxable Income")
                .ruleReference("Section 288A")
                .description("Gross Income minus Standard Deduction minus Chapter VI-A deductions (rounded to nearest ₹10)")
                .amount(netTaxableIncome)
                .formattedDetails(String.format("Taxable Income: ₹%,.0f", netTaxableIncome))
                .build());

        // 5. Slab Computation
        List<SlabBreakdown> slabBreakdowns = calculateOldRegimeSlabs(netTaxableIncome, ageCategory);
        double taxOnSlabs = slabBreakdowns.stream().mapToDouble(SlabBreakdown::getTaxForSlab).sum();

        trace.add(AuditTraceStep.builder()
                .stepNumber(stepNum++)
                .stepName("Tax on Slabs")
                .ruleReference("Finance Act Slabs (Old Regime)")
                .description(String.format("Progressive tax slab calculation for %s", ageCategory.getDescription()))
                .amount(taxOnSlabs)
                .formattedDetails(String.format("Tax before Rebate: ₹%,.0f", taxOnSlabs))
                .build());

        // 6. Section 87A Rebate (Old Regime: Net Taxable Income <= ₹5,00,000, Max Rebate ₹12,500)
        double rebate87A = 0.0;
        if (netTaxableIncome <= 500000.0 && taxOnSlabs > 0) {
            rebate87A = Math.min(taxOnSlabs, 12500.0);
            trace.add(AuditTraceStep.builder()
                    .stepNumber(stepNum++)
                    .stepName("Rebate under Section 87A")
                    .ruleReference("Section 87A")
                    .description("Full tax rebate for resident individual with Net Taxable Income <= ₹5,00,000 (Max ₹12,500)")
                    .amount(rebate87A)
                    .formattedDetails(String.format("Rebate: -₹%,.0f", rebate87A))
                    .build());
        }

        double taxAfterRebate = Math.max(0, taxOnSlabs - rebate87A);

        // 7. Surcharge & Marginal Relief
        SurchargeCalculationResult surchargeResult = calculateSurcharge(netTaxableIncome, taxAfterRebate, TaxRegime.OLD);
        if (surchargeResult.amount() > 0) {
            trace.add(AuditTraceStep.builder()
                    .stepNumber(stepNum++)
                    .stepName(String.format("Surcharge (%.0f%%)", surchargeResult.rate() * 100))
                    .ruleReference("Chapter II - Rates of Income Tax")
                    .description(String.format("Surcharge on high income > threshold (Marginal relief: ₹%,.0f)", surchargeResult.marginalRelief()))
                    .amount(surchargeResult.amount())
                    .formattedDetails(String.format("Surcharge: ₹%,.0f", surchargeResult.amount()))
                    .build());
        }

        // 8. Health & Education Cess @ 4%
        double cess = calculateCess(surchargeResult.taxPlusSurcharge());
        if (cess > 0) {
            trace.add(AuditTraceStep.builder()
                    .stepNumber(stepNum++)
                    .stepName("Health and Education Cess (4%)")
                    .ruleReference("Finance Act (4% Cess)")
                    .description("4% cess levied on total of Income Tax and Surcharge")
                    .amount(cess)
                    .formattedDetails(String.format("Cess: ₹%,.0f", cess))
                    .build());
        }

        // 9. Total Tax Payable (Rounded to nearest 10 as per Sec 288B)
        double totalTaxPayable = roundToNearestTen(surchargeResult.taxPlusSurcharge() + cess);
        double effectiveRate = grossTotalIncome > 0 ? (totalTaxPayable / grossTotalIncome) * 100.0 : 0.0;

        trace.add(AuditTraceStep.builder()
                .stepNumber(stepNum)
                .stepName("Total Tax Payable")
                .ruleReference("Section 288B")
                .description("Total tax payable rounded off to the nearest multiple of ₹10")
                .amount(totalTaxPayable)
                .formattedDetails(String.format("Final Tax: ₹%,.0f (Effective: %.2f%%)", totalTaxPayable, effectiveRate))
                .build());

        return TaxCalculationResult.builder()
                .regime(TaxRegime.OLD)
                .assessmentYear(getAssessmentYear())
                .ageCategory(ageCategory)
                .ruleEngineVersion(VERSION)
                .grossTotalIncome(grossTotalIncome)
                .standardDeduction(standardDeductionAllowed)
                .totalDeductionsAllowed(totalDeductions)
                .netTaxableIncome(netTaxableIncome)
                .taxOnSlabs(taxOnSlabs)
                .slabBreakdowns(slabBreakdowns)
                .rebateSection87A(rebate87A)
                .taxAfterRebate(taxAfterRebate)
                .surchargeRate(surchargeResult.rate())
                .surchargeAmount(surchargeResult.amount())
                .marginalReliefSurcharge(surchargeResult.marginalRelief())
                .healthAndEducationCess(cess)
                .totalTaxPayable(totalTaxPayable)
                .effectiveTaxRate(effectiveRate)
                .auditTrace(trace)
                .build();
    }

    @Override
    public TaxCalculationResult calculateNewRegime(TaxCalculationRequest request) {
        IncomeDetails income = request.getIncome() != null ? request.getIncome() : new IncomeDetails();
        DeductionDetails deductions = request.getDeductions() != null ? request.getDeductions() : new DeductionDetails();

        List<AuditTraceStep> trace = new ArrayList<>();
        int stepNum = 1;

        // 1. Gross Total Income (Note: Under 115BAC, loss from self-occupied property cannot be set off)
        double grossSalary = income.getGrossSalary();
        double housePropIncome = Math.max(0, income.getIncomeFromHouseProperty()); // Loss from self-occupied not allowed

        double grossTotalIncome = grossSalary + housePropIncome + income.getBusinessIncome() +
                income.getShortTermCapitalGains() + income.getLongTermCapitalGains() + income.getIncomeFromOtherSources();

        trace.add(AuditTraceStep.builder()
                .stepNumber(stepNum++)
                .stepName("Gross Total Income (New Regime)")
                .ruleReference("Section 115BAC")
                .description("Sum of all income sources (Loss from self-occupied house property not permitted)")
                .amount(grossTotalIncome)
                .formattedDetails(String.format("Gross Income: ₹%,.0f", grossTotalIncome))
                .build());

        // 2. Standard Deduction under Section 115BAC(1A)
        double standardDeductionAllowed = (grossSalary > 0) ? Math.min(grossSalary, STANDARD_DEDUCTION) : 0.0;
        if (standardDeductionAllowed > 0) {
            trace.add(AuditTraceStep.builder()
                    .stepNumber(stepNum++)
                    .stepName("Standard Deduction")
                    .ruleReference("Section 16(ia) read with Section 115BAC(1A)")
                    .description("Standard deduction of ₹50,000 allowed for salaried individuals in AY 2024-25")
                    .amount(standardDeductionAllowed)
                    .formattedDetails(String.format("Standard Deduction: ₹%,.0f", standardDeductionAllowed))
                    .build());
        }

        // 3. Permissible Deductions (Employer NPS under 80CCD(2))
        DeductionCalculator.DeductionCalculationResult deductionResult = deductionCalculator.calculateNewRegimeDeductions(deductions);
        double totalDeductions = deductionResult.totalDeductions();
        trace.addAll(deductionResult.auditSteps());

        // 4. Net Taxable Income
        double incomeAfterStd = Math.max(0, grossTotalIncome - standardDeductionAllowed);
        double netTaxableIncome = Math.max(0, incomeAfterStd - totalDeductions);
        netTaxableIncome = roundToNearestTen(netTaxableIncome);

        trace.add(AuditTraceStep.builder()
                .stepNumber(stepNum++)
                .stepName("Net Taxable Income")
                .ruleReference("Section 288A")
                .description("Gross Income minus Standard Deduction minus 80CCD(2) (rounded to nearest ₹10)")
                .amount(netTaxableIncome)
                .formattedDetails(String.format("Taxable Income: ₹%,.0f", netTaxableIncome))
                .build());

        // 5. New Regime Slab Calculation (AY 2024-25)
        List<SlabBreakdown> slabBreakdowns = calculateNewRegimeSlabs(netTaxableIncome);
        double taxOnSlabs = slabBreakdowns.stream().mapToDouble(SlabBreakdown::getTaxForSlab).sum();

        trace.add(AuditTraceStep.builder()
                .stepNumber(stepNum++)
                .stepName("Tax on Slabs (Section 115BAC)")
                .ruleReference("Section 115BAC(1A) Slabs")
                .description("AY 2024-25 Concessional Slabs: 0-3L (0%), 3-6L (5%), 6-9L (10%), 9-12L (15%), 12-15L (20%), >15L (30%)")
                .amount(taxOnSlabs)
                .formattedDetails(String.format("Tax before Rebate: ₹%,.0f", taxOnSlabs))
                .build());

        // 6. Section 87A Rebate & Marginal Relief for New Regime
        // Under 115BAC: If Net Taxable Income <= 7,00,000 => Full Rebate (Tax = 0)
        // If Net Taxable Income > 7,00,000, Marginal Relief ensures Tax <= (Taxable Income - 7,00,000)
        double rebate87A = 0.0;
        if (netTaxableIncome <= 700000.0 && taxOnSlabs > 0) {
            rebate87A = taxOnSlabs;
            trace.add(AuditTraceStep.builder()
                    .stepNumber(stepNum++)
                    .stepName("Section 87A Rebate (100% Tax Free)")
                    .ruleReference("Section 87A proviso")
                    .description("Full tax rebate for resident individual with taxable income up to ₹7,00,000")
                    .amount(rebate87A)
                    .formattedDetails(String.format("Rebate: -₹%,.0f", rebate87A))
                    .build());
        } else if (netTaxableIncome > 700000.0 && taxOnSlabs > 0) {
            double excessIncomeOver7L = netTaxableIncome - 700000.0;
            if (taxOnSlabs > excessIncomeOver7L) {
                rebate87A = taxOnSlabs - excessIncomeOver7L;
                trace.add(AuditTraceStep.builder()
                        .stepNumber(stepNum++)
                        .stepName("Section 87A Marginal Relief")
                        .ruleReference("Section 87A Proviso (Marginal Relief)")
                        .description(String.format("Tax cannot exceed income exceeding ₹7L (₹%,.0f). Marginal rebate of ₹%,.0f applied.",
                                excessIncomeOver7L, rebate87A))
                        .amount(rebate87A)
                        .formattedDetails(String.format("Marginal Rebate: -₹%,.0f", rebate87A))
                        .build());
            }
        }

        double taxAfterRebate = Math.max(0, taxOnSlabs - rebate87A);

        // 7. Surcharge & Marginal Relief
        SurchargeCalculationResult surchargeResult = calculateSurcharge(netTaxableIncome, taxAfterRebate, TaxRegime.NEW);
        if (surchargeResult.amount() > 0) {
            trace.add(AuditTraceStep.builder()
                    .stepNumber(stepNum++)
                    .stepName(String.format("Surcharge (%.0f%%)", surchargeResult.rate() * 100))
                    .ruleReference("Section 115BAC Surcharge")
                    .description(String.format("Surcharge on income > threshold (Marginal relief: ₹%,.0f)", surchargeResult.marginalRelief()))
                    .amount(surchargeResult.amount())
                    .formattedDetails(String.format("Surcharge: ₹%,.0f", surchargeResult.amount()))
                    .build());
        }

        // 8. Health & Education Cess @ 4%
        double cess = calculateCess(surchargeResult.taxPlusSurcharge());
        if (cess > 0) {
            trace.add(AuditTraceStep.builder()
                    .stepNumber(stepNum++)
                    .stepName("Health and Education Cess (4%)")
                    .ruleReference("Finance Act (4% Cess)")
                    .description("4% cess levied on total of Income Tax and Surcharge")
                    .amount(cess)
                    .formattedDetails(String.format("Cess: ₹%,.0f", cess))
                    .build());
        }

        // 9. Total Tax Payable
        double totalTaxPayable = roundToNearestTen(surchargeResult.taxPlusSurcharge() + cess);
        double effectiveRate = grossTotalIncome > 0 ? (totalTaxPayable / grossTotalIncome) * 100.0 : 0.0;

        trace.add(AuditTraceStep.builder()
                .stepNumber(stepNum)
                .stepName("Total Tax Payable (New Regime)")
                .ruleReference("Section 288B")
                .description("Total tax payable rounded off to nearest ₹10")
                .amount(totalTaxPayable)
                .formattedDetails(String.format("Final Tax: ₹%,.0f (Effective: %.2f%%)", totalTaxPayable, effectiveRate))
                .build());

        return TaxCalculationResult.builder()
                .regime(TaxRegime.NEW)
                .assessmentYear(getAssessmentYear())
                .ageCategory(request.getAgeCategory())
                .ruleEngineVersion(VERSION)
                .grossTotalIncome(grossTotalIncome)
                .standardDeduction(standardDeductionAllowed)
                .totalDeductionsAllowed(totalDeductions)
                .netTaxableIncome(netTaxableIncome)
                .taxOnSlabs(taxOnSlabs)
                .slabBreakdowns(slabBreakdowns)
                .rebateSection87A(rebate87A)
                .taxAfterRebate(taxAfterRebate)
                .surchargeRate(surchargeResult.rate())
                .surchargeAmount(surchargeResult.amount())
                .marginalReliefSurcharge(surchargeResult.marginalRelief())
                .healthAndEducationCess(cess)
                .totalTaxPayable(totalTaxPayable)
                .effectiveTaxRate(effectiveRate)
                .auditTrace(trace)
                .build();
    }

    public List<SlabBreakdown> calculateOldRegimeSlabs(double taxableIncome, AgeCategory ageCategory) {
        List<SlabBreakdown> slabs = new ArrayList<>();
        double basicExemption;
        switch (ageCategory) {
            case SENIOR_60_TO_80 -> basicExemption = 300000.0;
            case SUPER_SENIOR_ABOVE_80 -> basicExemption = 500000.0;
            default -> basicExemption = 250000.0;
        }

        // Slab 1: Up to Basic Exemption (0%)
        double s1Amount = Math.min(taxableIncome, basicExemption);
        slabs.add(SlabBreakdown.builder()
                .slabRange(String.format("₹0 - ₹%,.0f", basicExemption))
                .minIncome(0).maxIncome(basicExemption)
                .taxableAmountInSlab(s1Amount)
                .ratePercentage(0.0).taxForSlab(0.0).build());

        // Slab 2: Basic Exemption to 5,00,000 (5%)
        if (basicExemption < 500000.0) {
            double s2Taxable = Math.max(0, Math.min(taxableIncome, 500000.0) - basicExemption);
            double s2Tax = s2Taxable * 0.05;
            slabs.add(SlabBreakdown.builder()
                    .slabRange(String.format("₹%,.0f - ₹5,00,000", basicExemption))
                    .minIncome(basicExemption).maxIncome(500000.0)
                    .taxableAmountInSlab(s2Taxable)
                    .ratePercentage(5.0).taxForSlab(s2Tax).build());
        }

        // Slab 3: 5,00,001 to 10,00,000 (20%)
        double s3Taxable = Math.max(0, Math.min(taxableIncome, 1000000.0) - 500000.0);
        double s3Tax = s3Taxable * 0.20;
        slabs.add(SlabBreakdown.builder()
                .slabRange("₹5,00,001 - ₹10,00,000")
                .minIncome(500000.0).maxIncome(1000000.0)
                .taxableAmountInSlab(s3Taxable)
                .ratePercentage(20.0).taxForSlab(s3Tax).build());

        // Slab 4: Above 10,00,000 (30%)
        double s4Taxable = Math.max(0, taxableIncome - 1000000.0);
        double s4Tax = s4Taxable * 0.30;
        slabs.add(SlabBreakdown.builder()
                .slabRange("Above ₹10,00,000")
                .minIncome(1000000.0).maxIncome(Double.MAX_VALUE)
                .taxableAmountInSlab(s4Taxable)
                .ratePercentage(30.0).taxForSlab(s4Tax).build());

        return slabs;
    }

    public List<SlabBreakdown> calculateNewRegimeSlabs(double taxableIncome) {
        List<SlabBreakdown> slabs = new ArrayList<>();

        // S1: 0 - 3L (0%)
        double s1 = Math.min(taxableIncome, 300000.0);
        slabs.add(SlabBreakdown.builder()
                .slabRange("₹0 - ₹3,00,000")
                .minIncome(0).maxIncome(300000.0)
                .taxableAmountInSlab(s1).ratePercentage(0.0).taxForSlab(0.0).build());

        // S2: 3L - 6L (5%)
        double s2 = Math.max(0, Math.min(taxableIncome, 600000.0) - 300000.0);
        slabs.add(SlabBreakdown.builder()
                .slabRange("₹3,00,001 - ₹6,00,000")
                .minIncome(300000.0).maxIncome(600000.0)
                .taxableAmountInSlab(s2).ratePercentage(5.0).taxForSlab(s2 * 0.05).build());

        // S3: 6L - 9L (10%)
        double s3 = Math.max(0, Math.min(taxableIncome, 900000.0) - 600000.0);
        slabs.add(SlabBreakdown.builder()
                .slabRange("₹6,00,001 - ₹9,00,000")
                .minIncome(600000.0).maxIncome(900000.0)
                .taxableAmountInSlab(s3).ratePercentage(10.0).taxForSlab(s3 * 0.10).build());

        // S4: 9L - 12L (15%)
        double s4 = Math.max(0, Math.min(taxableIncome, 1200000.0) - 900000.0);
        slabs.add(SlabBreakdown.builder()
                .slabRange("₹9,00,001 - ₹12,00,000")
                .minIncome(900000.0).maxIncome(1200000.0)
                .taxableAmountInSlab(s4).ratePercentage(15.0).taxForSlab(s4 * 0.15).build());

        // S5: 12L - 15L (20%)
        double s5 = Math.max(0, Math.min(taxableIncome, 1500000.0) - 1200000.0);
        slabs.add(SlabBreakdown.builder()
                .slabRange("₹12,00,001 - ₹15,00,000")
                .minIncome(1200000.0).maxIncome(1500000.0)
                .taxableAmountInSlab(s5).ratePercentage(20.0).taxForSlab(s5 * 0.20).build());

        // S6: Above 15L (30%)
        double s6 = Math.max(0, taxableIncome - 1500000.0);
        slabs.add(SlabBreakdown.builder()
                .slabRange("Above ₹15,00,000")
                .minIncome(1500000.0).maxIncome(Double.MAX_VALUE)
                .taxableAmountInSlab(s6).ratePercentage(30.0).taxForSlab(s6 * 0.30).build());

        return slabs;
    }

    @Override
    protected double calculateTaxOnIncome(double taxableIncome, TaxRegime regime) {
        if (regime == TaxRegime.NEW) {
            return calculateNewRegimeSlabs(taxableIncome).stream().mapToDouble(SlabBreakdown::getTaxForSlab).sum();
        } else {
            return calculateOldRegimeSlabs(taxableIncome, AgeCategory.BELOW_60).stream().mapToDouble(SlabBreakdown::getTaxForSlab).sum();
        }
    }
}
