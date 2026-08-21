package com.cleartaxer.service;

import com.cleartaxer.engine.DeductionCalculator;
import com.cleartaxer.engine.RuleEngineRegistry;
import com.cleartaxer.engine.TaxRuleEngine;
import com.cleartaxer.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TaxCalculatorService {

    private final RuleEngineRegistry ruleEngineRegistry;

    public TaxCalculationResult calculateSingleRegime(TaxCalculationRequest request, TaxRegime regime) {
        AssessmentYear ay = request.getAssessmentYear() != null ? request.getAssessmentYear() : AssessmentYear.AY_2024_25;
        TaxRuleEngine engine = ruleEngineRegistry.getEngine(ay);
        return engine.calculate(request, regime);
    }

    public RegimeComparisonResult compareRegimes(TaxCalculationRequest request) {
        AssessmentYear ay = request.getAssessmentYear() != null ? request.getAssessmentYear() : AssessmentYear.AY_2024_25;
        TaxRuleEngine engine = ruleEngineRegistry.getEngine(ay);

        TaxCalculationResult oldCalc = engine.calculateOldRegime(request);
        TaxCalculationResult newCalc = engine.calculateNewRegime(request);

        double oldTax = oldCalc.getTotalTaxPayable();
        double newTax = newCalc.getTotalTaxPayable();

        TaxRegime recommended;
        double taxSaved;
        String summary;

        if (newTax < oldTax) {
            recommended = TaxRegime.NEW;
            taxSaved = oldTax - newTax;
            summary = String.format("New Tax Regime (Section 115BAC) is more beneficial for you, saving ₹%,.0f in total tax.", taxSaved);
        } else if (oldTax < newTax) {
            recommended = TaxRegime.OLD;
            taxSaved = newTax - oldTax;
            summary = String.format("Old Tax Regime is more beneficial for you due to eligible deductions, saving ₹%,.0f in total tax.", taxSaved);
        } else {
            recommended = TaxRegime.NEW; // Default default regime in India
            taxSaved = 0.0;
            summary = "Both Old and New Regimes result in the exact same tax liability. New Regime is the default regime.";
        }

        // Optimization suggestions
        List<String> tips = generateDeductionOptimizationTips(request, oldCalc, newCalc);

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("oldTaxPayable", oldTax);
        metrics.put("newTaxPayable", newTax);
        metrics.put("oldEffectiveRate", oldCalc.getEffectiveTaxRate());
        metrics.put("newEffectiveRate", newCalc.getEffectiveTaxRate());
        metrics.put("oldTotalDeductions", oldCalc.getTotalDeductionsAllowed());
        metrics.put("newTotalDeductions", newCalc.getTotalDeductionsAllowed());
        metrics.put("taxDifference", Math.abs(oldTax - newTax));

        return RegimeComparisonResult.builder()
                .assessmentYear(ay)
                .oldRegime(oldCalc)
                .newRegime(newCalc)
                .recommendedRegime(recommended)
                .taxSaved(taxSaved)
                .recommendationSummary(summary)
                .optimizationTips(tips)
                .comparisonMetrics(metrics)
                .build();
    }

    private List<String> generateDeductionOptimizationTips(
            TaxCalculationRequest request,
            TaxCalculationResult oldCalc,
            TaxCalculationResult newCalc) {

        List<String> tips = new ArrayList<>();
        DeductionDetails d = request.getDeductions() != null ? request.getDeductions() : new DeductionDetails();

        // 80C Headroom
        double unused80C = Math.max(0, DeductionCalculator.MAX_80C - d.getSection80C());
        if (unused80C > 0) {
            tips.add(String.format("Unclaimed Section 80C headroom: ₹%,.0f (via PPF, ELSS, EPF, or Life Insurance).", unused80C));
        }

        // 80CCD(1B) NPS Headroom
        double unusedNps = Math.max(0, DeductionCalculator.MAX_80CCD_1B - d.getSection80CCD1B());
        if (unusedNps > 0) {
            tips.add(String.format("Additional NPS benefit under 80CCD(1B): ₹%,.0f headroom available for extra tax reduction.", unusedNps));
        }

        // 80D Health Insurance Headroom
        double unusedHealthSelf = Math.max(0, 25000.0 - d.getSection80DSelf());
        if (unusedHealthSelf > 0) {
            tips.add(String.format("Section 80D (Self & Family): Up to ₹%,.0f medical insurance premium can be claimed.", unusedHealthSelf));
        }

        if (d.getSection80DParents() == 0) {
            tips.add("Section 80D (Parents): You can claim up to ₹25,000 (or ₹50,000 if senior citizen parents) for parents' health coverage.");
        }

        // Employer NPS 80CCD(2)
        if (d.getSection80CCD2() == 0) {
            tips.add("Section 80CCD(2): Corporate NPS contributions made by your employer are exempt in BOTH Old and New Regimes (up to 10% of Basic+DA).");
        }

        return tips;
    }
}
