package com.cleartaxer.engine;

import com.cleartaxer.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PresumptiveTaxEngine {

    public static final double SEC_44ADA_BASE_LIMIT = 5000000.0;    // 50 Lakhs
    public static final double SEC_44ADA_DIGITAL_LIMIT = 7500000.0; // 75 Lakhs (if cash <= 5%)

    public static final double SEC_44AD_BASE_LIMIT = 20000000.0;    // 2 Crores
    public static final double SEC_44AD_DIGITAL_LIMIT = 30000000.0; // 3 Crores (if cash <= 5%)

    public static final double GST_THRESHOLD_SERVICES = 2000000.0;  // 20 Lakhs
    public static final double GST_THRESHOLD_GOODS = 4000000.0;     // 40 Lakhs

    private final RuleEngineRegistry ruleEngineRegistry;

    public PresumptiveTaxResult calculate(PresumptiveTaxRequest request) {
        PresumptiveSection section = request.getSection() != null ? request.getSection() : PresumptiveSection.SECTION_44ADA;
        AssessmentYear ay = request.getAssessmentYear() != null ? request.getAssessmentYear() : AssessmentYear.AY_2024_25;

        double digital = Math.max(0, request.getDigitalReceipts());
        double cash = Math.max(0, request.getCashReceipts());
        double totalReceipts = digital + cash;

        double digitalPercentage = totalReceipts > 0 ? (digital / totalReceipts) * 100.0 : 100.0;
        boolean qualifiesForHigherLimit = digitalPercentage >= 95.0;

        double limit;
        double deemedProfit;
        double deemedProfitRate;
        boolean isEligible;
        String eligibilityReason;

        if (section == PresumptiveSection.SECTION_44ADA) {
            limit = qualifiesForHigherLimit ? SEC_44ADA_DIGITAL_LIMIT : SEC_44ADA_BASE_LIMIT;
            isEligible = totalReceipts <= limit;
            if (isEligible) {
                eligibilityReason = String.format("Eligible under Section 44ADA (Gross receipts ₹%,.0f are within limit of ₹%,.0f)",
                        totalReceipts, limit);
            } else {
                eligibilityReason = String.format("Gross receipts ₹%,.0f exceed the Section 44ADA limit of ₹%,.0f. Tax audit under Sec 44AB required if profits < 50%%.",
                        totalReceipts, limit);
            }

            // Minimum 50% profit
            double baseRate = 0.50;
            if (request.getCustomProfitPercentage() != null && request.getCustomProfitPercentage() >= 50.0) {
                deemedProfitRate = request.getCustomProfitPercentage() / 100.0;
            } else {
                deemedProfitRate = baseRate;
            }
            deemedProfit = totalReceipts * deemedProfitRate;

        } else { // SECTION_44AD
            limit = qualifiesForHigherLimit ? SEC_44AD_DIGITAL_LIMIT : SEC_44AD_BASE_LIMIT;
            isEligible = totalReceipts <= limit;
            if (isEligible) {
                eligibilityReason = String.format("Eligible under Section 44AD (Turnover ₹%,.0f is within limit of ₹%,.0f)",
                        totalReceipts, limit);
            } else {
                eligibilityReason = String.format("Turnover ₹%,.0f exceeds the Section 44AD limit of ₹%,.0f. Tax audit under Sec 44AB required.",
                        totalReceipts, limit);
            }

            if (request.getCustomProfitPercentage() != null && request.getCustomProfitPercentage() > 0) {
                deemedProfitRate = request.getCustomProfitPercentage() / 100.0;
                deemedProfit = totalReceipts * deemedProfitRate;
            } else {
                // 6% on digital + 8% on cash
                double digitalProfit = digital * 0.06;
                double cashProfit = cash * 0.08;
                deemedProfit = digitalProfit + cashProfit;
                deemedProfitRate = totalReceipts > 0 ? (deemedProfit / totalReceipts) : 0.06;
            }
        }

        // Run full tax calculation on the deemed profit + other income
        IncomeDetails income = IncomeDetails.builder()
                .businessIncome(deemedProfit)
                .incomeFromOtherSources(request.getOtherIncome())
                .build();

        TaxCalculationRequest calcRequest = TaxCalculationRequest.builder()
                .assessmentYear(ay)
                .ageCategory(AgeCategory.BELOW_60)
                .income(income)
                .deductions(request.getDeductions())
                .build();

        TaxRuleEngine engine = ruleEngineRegistry.getEngine(ay);
        TaxCalculationResult oldRegimeCalc = engine.calculateOldRegime(calcRequest);
        TaxCalculationResult newRegimeCalc = engine.calculateNewRegime(calcRequest);

        TaxRegime recommended = (newRegimeCalc.getTotalTaxPayable() <= oldRegimeCalc.getTotalTaxPayable())
                ? TaxRegime.NEW : TaxRegime.OLD;
        double finalTax = (recommended == TaxRegime.NEW)
                ? newRegimeCalc.getTotalTaxPayable()
                : oldRegimeCalc.getTotalTaxPayable();

        // Advance Tax Schedule
        List<AdvanceTaxInstallment> advanceTaxSchedule = generateAdvanceTaxSchedule(section, finalTax);

        // GST Applicability Check
        double gstThreshold = (section == PresumptiveSection.SECTION_44ADA) ? GST_THRESHOLD_SERVICES : GST_THRESHOLD_GOODS;
        boolean gstMandatory = totalReceipts > gstThreshold;
        String gstNote;
        if (gstMandatory) {
            gstNote = String.format("Gross turnover of ₹%,.0f exceeds the GST threshold of ₹%,.0f. GST registration is mandatory.",
                    totalReceipts, gstThreshold);
        } else {
            gstNote = String.format("Gross turnover is below ₹%,.0f. Normal domestic GST registration is optional (Unless exporting services / inter-state supply).",
                    gstThreshold);
        }

        return PresumptiveTaxResult.builder()
                .section(section)
                .assessmentYear(ay)
                .totalGrossReceipts(totalReceipts)
                .digitalReceipts(digital)
                .cashReceipts(cash)
                .digitalPercentage(digitalPercentage)
                .eligibilityThreshold(limit)
                .isEligible(isEligible)
                .eligibilityReason(eligibilityReason)
                .deemedProfitRate(deemedProfitRate * 100.0)
                .deemedProfitAmount(deemedProfit)
                .totalGrossIncome(deemedProfit + request.getOtherIncome())
                .oldRegimeCalculation(oldRegimeCalc)
                .newRegimeCalculation(newRegimeCalc)
                .recommendedRegime(recommended)
                .taxPayable(finalTax)
                .advanceTaxSchedule(advanceTaxSchedule)
                .gstThreshold(gstThreshold)
                .gstRegistrationMandatory(gstMandatory)
                .gstApplicabilityNote(gstNote)
                .build();
    }

    private List<AdvanceTaxInstallment> generateAdvanceTaxSchedule(PresumptiveSection section, double totalTaxPayable) {
        List<AdvanceTaxInstallment> schedule = new ArrayList<>();

        if (totalTaxPayable < 10000.0) {
            schedule.add(AdvanceTaxInstallment.builder()
                    .installmentNumber(1)
                    .dueDate("15th March of FY")
                    .cumulativePercentage(100.0)
                    .cumulativeAmountDue(0.0)
                    .installmentAmount(0.0)
                    .note("Advance tax is NOT applicable since total net tax liability is under ₹10,000 (Section 208)")
                    .build());
            return schedule;
        }

        // Section 44ADA & 44AD filers pay 100% advance tax by 15th March in one installment as per Section 211(1)(b)
        schedule.add(AdvanceTaxInstallment.builder()
                .installmentNumber(1)
                .dueDate("15th June of FY")
                .cumulativePercentage(15.0)
                .cumulativeAmountDue(0.0)
                .installmentAmount(0.0)
                .note("Exempt under Presumptive Section 44AD / 44ADA. Single installment allowed.")
                .build());

        schedule.add(AdvanceTaxInstallment.builder()
                .installmentNumber(2)
                .dueDate("15th September of FY")
                .cumulativePercentage(45.0)
                .cumulativeAmountDue(0.0)
                .installmentAmount(0.0)
                .note("Exempt under Presumptive Scheme.")
                .build());

        schedule.add(AdvanceTaxInstallment.builder()
                .installmentNumber(3)
                .dueDate("15th December of FY")
                .cumulativePercentage(75.0)
                .cumulativeAmountDue(0.0)
                .installmentAmount(0.0)
                .note("Exempt under Presumptive Scheme.")
                .build());

        schedule.add(AdvanceTaxInstallment.builder()
                .installmentNumber(4)
                .dueDate("15th March of FY")
                .cumulativePercentage(100.0)
                .cumulativeAmountDue(totalTaxPayable)
                .installmentAmount(totalTaxPayable)
                .note("100% of Advance Tax must be paid on or before 15th March to avoid interest under 234C.")
                .build());

        return schedule;
    }
}
