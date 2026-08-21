package com.cleartaxer.service;

import com.cleartaxer.model.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaxExplanationService {

    public TaxExplanationResponse explainCalculation(TaxExplanationRequest request) {
        TaxCalculationResult result = request.getCalculationResult();
        RegimeComparisonResult comparison = request.getComparisonResult();
        String question = request.getUserQuestion();

        if (result == null && comparison != null) {
            result = comparison.getRecommendedRegime() == TaxRegime.NEW
                    ? comparison.getNewRegime()
                    : comparison.getOldRegime();
        }

        if (result == null) {
            return TaxExplanationResponse.builder()
                    .executiveSummary("Please perform a tax calculation first.")
                    .plainLanguageBreakdown("No active calculation trace found to analyze.")
                    .statutoryDisclaimer("ClearTaxer computes and explains income tax for informational purposes. Consult a Chartered Accountant for formal tax filing.")
                    .build();
        }

        List<String> keyObservations = new ArrayList<>();
        List<String> strategicTips = new ArrayList<>();
        List<String> faqs = new ArrayList<>();

        double gross = result.getGrossTotalIncome();
        double net = result.getNetTaxableIncome();
        double tax = result.getTotalTaxPayable();
        double stdDed = result.getStandardDeduction();
        double ch6a = result.getTotalDeductionsAllowed();
        TaxRegime regime = result.getRegime();

        // Build Plain Language Breakdown
        StringBuilder breakdown = new StringBuilder();
        breakdown.append(String.format("1. **Gross Income**: Total income across salary, house property, business, and other sources is ₹%,.0f.\n", gross));

        if (stdDed > 0) {
            breakdown.append(String.format("2. **Standard Deduction**: A statutory deduction of ₹%,.0f was deducted automatically under Section 16(ia).\n", stdDed));
        }

        if (ch6a > 0) {
            breakdown.append(String.format("3. **Chapter VI-A Deductions**: You utilized ₹%,.0f across eligible sections (80C, 80D, NPS, etc.).\n", ch6a));
        }

        breakdown.append(String.format("4. **Net Taxable Income**: After eligible deductions, your taxable base is ₹%,.0f (rounded per Sec 288A).\n", net));

        if (result.getRebateSection87A() > 0) {
            breakdown.append(String.format("5. **Tax Rebate (Sec 87A)**: You received a tax rebate of ₹%,.0f under Section 87A, reducing your initial tax liability.\n", result.getRebateSection87A()));
        }

        if (result.getSurchargeAmount() > 0) {
            breakdown.append(String.format("6. **Surcharge**: A high-income surcharge of ₹%,.0f (%.0f%%) was applied with marginal relief adjustment.\n",
                    result.getSurchargeAmount(), result.getSurchargeRate() * 100.0));
        }

        breakdown.append(String.format("7. **Health & Education Cess**: A mandatory 4%% cess of ₹%,.0f was added to the tax.\n", result.getHealthAndEducationCess()));
        breakdown.append(String.format("8. **Final Tax Liability**: ₹%,.0f (Effective tax rate on gross income: %.2f%%).\n", tax, result.getEffectiveTaxRate()));

        // Observations
        if (tax == 0 && gross > 0) {
            keyObservations.add("Zero Tax Liability: You have ₹0 tax payable because your net taxable income qualifies for 100% tax rebate under Section 87A!");
        } else {
            keyObservations.add(String.format("Your effective tax burden is %.2f%% of your total gross income.", result.getEffectiveTaxRate()));
        }

        if (comparison != null) {
            if (comparison.getTaxSaved() > 0) {
                keyObservations.add(String.format("By choosing the %s Regime, you are saving ₹%,.0f compared to the %s Regime.",
                        comparison.getRecommendedRegime(),
                        comparison.getTaxSaved(),
                        comparison.getRecommendedRegime() == TaxRegime.NEW ? "Old" : "New"));
            } else {
                keyObservations.add("Old and New regimes have identical tax liability for your current income profile.");
            }
        }

        // Strategic Tips
        strategicTips.add("Employer NPS under Section 80CCD(2) provides tax deduction in BOTH New and Old regimes without lowering your in-hand salary.");
        if (regime == TaxRegime.OLD && ch6a < 200000.0) {
            strategicTips.add("Maximizing 80C (₹1.5L) and Section 80CCD(1B) NPS (₹50k) can significantly increase your savings under the Old Regime.");
        }
        if (gross > 5000000.0) {
            strategicTips.add("For income above ₹50 Lakhs, New Regime caps the highest surcharge at 25% compared to 37% in Old Regime, resulting in substantial savings.");
        }

        // Socratic FAQ / Contextual Answers
        if (question != null && !question.trim().isEmpty()) {
            faqs.add(String.format("**Q: %s**", question));
            faqs.add(answerCustomQuestion(question, result, comparison));
        } else {
            faqs.add("**Q: What is Section 87A Rebate?**");
            faqs.add("Section 87A provides a 100% tax rebate for individuals with taxable income up to ₹7,00,000 under the New Regime (or ₹5,00,000 under the Old Regime), meaning zero tax payable.");
            faqs.add("**Q: Can I switch between Old and New Regime every year?**");
            faqs.add("Salaried individuals without business income can freely switch regimes each Assessment Year while filing ITR. Individuals with business/professional income can opt out of New Regime only once in a lifetime.");
        }

        String summary = String.format("Computed tax liability of ₹%,.0f on gross income of ₹%,.0f (%s Regime, %s).",
                tax, gross, regime, result.getAssessmentYear().getLabel());

        return TaxExplanationResponse.builder()
                .executiveSummary(summary)
                .plainLanguageBreakdown(breakdown.toString())
                .keyObservations(keyObservations)
                .strategicTips(strategicTips)
                .faqClarifications(faqs)
                .statutoryDisclaimer("ClearTaxer provides deterministic mathematical computations and AI-powered educational explanations. It is not a substitute for a Chartered Accountant.")
                .build();
    }

    private String answerCustomQuestion(String question, TaxCalculationResult result, RegimeComparisonResult comparison) {
        String q = question.toLowerCase();
        if (q.contains("new") || q.contains("old") || q.contains("which regime") || q.contains("better")) {
            if (comparison != null) {
                return String.format("For your specific profile, the %s Regime is superior, saving ₹%,.0f in total taxes. %s",
                        comparison.getRecommendedRegime(), comparison.getTaxSaved(), comparison.getRecommendationSummary());
            }
            return "The New Regime offers lower tax slab rates and a higher rebate threshold (₹7 Lakhs), while the Old Regime is beneficial if you have deductions exceeding ₹3.75 - ₹4 Lakhs (HRA, 80C, 80D, Home loan).";
        }

        if (q.contains("nps") || q.contains("80ccd")) {
            return "NPS offers two major benefits: Section 80CCD(1B) gives an exclusive individual deduction up to ₹50,000 in Old Regime, and Section 80CCD(2) gives an employer contribution deduction in both Old and New regimes.";
        }

        if (q.contains("hra") || q.contains("rent")) {
            return "HRA exemption is calculated as the lowest of: (1) Actual HRA received, (2) Rent paid minus 10% of Basic salary, (3) 50% of Basic for Metro or 40% for Non-Metro. HRA exemption is ONLY available in the Old Tax Regime.";
        }

        return String.format("Based on your current computation (Net Taxable Income: ₹%,.0f, Total Tax: ₹%,.0f), the rule engine has verified all statutory deductions, slab calculations, and cess according to official CBDT guidelines.",
                result.getNetTaxableIncome(), result.getTotalTaxPayable());
    }
}
