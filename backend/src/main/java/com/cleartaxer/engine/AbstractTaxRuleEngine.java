package com.cleartaxer.engine;

import com.cleartaxer.model.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTaxRuleEngine implements TaxRuleEngine {

    public static final double HEALTH_AND_EDUCATION_CESS_RATE = 0.04;

    /**
     * Section 288A: Round to nearest multiple of ten rupees.
     */
    public static double roundToNearestTen(double amount) {
        return Math.round(amount / 10.0) * 10.0;
    }

    /**
     * Compute Health & Education Cess @ 4%.
     */
    protected double calculateCess(double taxPlusSurcharge) {
        return taxPlusSurcharge * HEALTH_AND_EDUCATION_CESS_RATE;
    }

    /**
     * Compute Surcharge and applicable Marginal Relief.
     */
    protected SurchargeCalculationResult calculateSurcharge(
            double netTaxableIncome,
            double taxAfterRebate,
            TaxRegime regime) {

        double surchargeRate = 0.0;
        double threshold = 0.0;

        if (regime == TaxRegime.NEW) {
            if (netTaxableIncome > 20000000.0) {
                surchargeRate = 0.25;
                threshold = 20000000.0;
            } else if (netTaxableIncome > 10000000.0) {
                surchargeRate = 0.15;
                threshold = 10000000.0;
            } else if (netTaxableIncome > 5000000.0) {
                surchargeRate = 0.10;
                threshold = 5000000.0;
            }
        } else {
            if (netTaxableIncome > 50000000.0) {
                surchargeRate = 0.37;
                threshold = 50000000.0;
            } else if (netTaxableIncome > 20000000.0) {
                surchargeRate = 0.25;
                threshold = 20000000.0;
            } else if (netTaxableIncome > 10000000.0) {
                surchargeRate = 0.15;
                threshold = 10000000.0;
            } else if (netTaxableIncome > 5000000.0) {
                surchargeRate = 0.10;
                threshold = 5000000.0;
            }
        }

        if (surchargeRate == 0.0) {
            return new SurchargeCalculationResult(0.0, 0.0, 0.0, taxAfterRebate);
        }

        double rawSurcharge = taxAfterRebate * surchargeRate;
        double taxPlusSurcharge = taxAfterRebate + rawSurcharge;

        // Marginal relief on surcharge
        // Total Tax + Surcharge cannot exceed: (Tax on threshold income + previous surcharge if any) + (Income - threshold)
        double taxOnThreshold = calculateTaxOnIncome(threshold, regime);
        double previousSurchargeRate = getSurchargeRateForThreshold(threshold, regime);
        double taxPlusSurchargeAtThreshold = taxOnThreshold * (1.0 + previousSurchargeRate);

        double excessIncome = netTaxableIncome - threshold;
        double maxAllowedTaxPlusSurcharge = taxPlusSurchargeAtThreshold + excessIncome;

        double marginalRelief = 0.0;
        if (taxPlusSurcharge > maxAllowedTaxPlusSurcharge) {
            marginalRelief = taxPlusSurcharge - maxAllowedTaxPlusSurcharge;
        }

        double finalSurcharge = Math.max(0, rawSurcharge - marginalRelief);
        double finalTaxPlusSurcharge = taxAfterRebate + finalSurcharge;

        return new SurchargeCalculationResult(surchargeRate, finalSurcharge, marginalRelief, finalTaxPlusSurcharge);
    }

    private double getSurchargeRateForThreshold(double threshold, TaxRegime regime) {
        if (threshold == 5000000.0) return 0.0;
        if (threshold == 10000000.0) return 0.10;
        if (threshold == 20000000.0) return 0.15;
        if (threshold == 50000000.0) return 0.25;
        return 0.0;
    }

    protected abstract double calculateTaxOnIncome(double taxableIncome, TaxRegime regime);

    public record SurchargeCalculationResult(
            double rate,
            double amount,
            double marginalRelief,
            double taxPlusSurcharge
    ) {}
}
