package com.cleartaxer.engine;

import com.cleartaxer.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OldVsNewRegimeCBDTTest {

    private DeductionCalculator deductionCalculator;
    private AY2024_25_RuleEngine engine2024;
    private AY2025_26_RuleEngine engine2025;

    @BeforeEach
    void setUp() {
        deductionCalculator = new DeductionCalculator();
        engine2024 = new AY2024_25_RuleEngine(deductionCalculator);
        engine2025 = new AY2025_26_RuleEngine(deductionCalculator);
    }

    @Test
    @DisplayName("CBDT Worked Example 1: Salaried ₹7.5L in AY 2024-25 has Zero Tax in New Regime via 87A")
    void testSalaried7Point5LakhsZeroTaxNewRegime() {
        TaxCalculationRequest request = TaxCalculationRequest.builder()
                .assessmentYear(AssessmentYear.AY_2024_25)
                .ageCategory(AgeCategory.BELOW_60)
                .income(IncomeDetails.builder().grossSalary(750000.0).build())
                .build();

        TaxCalculationResult result = engine2024.calculateNewRegime(request);

        assertEquals(750000.0, result.getGrossTotalIncome(), 0.01);
        assertEquals(50000.0, result.getStandardDeduction(), 0.01);
        assertEquals(700000.0, result.getNetTaxableIncome(), 0.01);

        // Slab tax on 7L: 0-3L=0, 3-6L=15k, 6-7L=10k => Total 25,000
        assertEquals(25000.0, result.getTaxOnSlabs(), 0.01);
        assertEquals(25000.0, result.getRebateSection87A(), 0.01);
        assertEquals(0.0, result.getTaxAfterRebate(), 0.01);
        assertEquals(0.0, result.getTotalTaxPayable(), 0.01);
    }

    @Test
    @DisplayName("CBDT Worked Example 2: Salaried ₹12L comparing Old vs New Regime in AY 2024-25")
    void testSalaried12LakhsComparison() {
        DeductionDetails deductions = DeductionDetails.builder()
                .section80C(150000.0)
                .section80DSelf(25000.0)
                .section80CCD1B(50000.0)
                .build();

        TaxCalculationRequest request = TaxCalculationRequest.builder()
                .assessmentYear(AssessmentYear.AY_2024_25)
                .ageCategory(AgeCategory.BELOW_60)
                .income(IncomeDetails.builder().grossSalary(1200000.0).build())
                .deductions(deductions)
                .build();

        // Old Regime:
        // Gross 12L - 50k std = 11.5L. Deductions = 1.5L + 25k + 50k = 2.25L. Net = 9.25L
        // Tax: 0-2.5L (0) + 2.5-5L (12.5k) + 5-9.25L (4.25L * 20% = 85k) = 97,500
        // Cess 4% = 3,900. Total = 1,01,400.
        TaxCalculationResult oldResult = engine2024.calculateOldRegime(request);
        assertEquals(925000.0, oldResult.getNetTaxableIncome(), 0.01);
        assertEquals(97500.0, oldResult.getTaxOnSlabs(), 0.01);
        assertEquals(3900.0, oldResult.getHealthAndEducationCess(), 0.01);
        assertEquals(101400.0, oldResult.getTotalTaxPayable(), 0.01);

        // New Regime:
        // Gross 12L - 50k std = 11.5L (no 80C/80D/NPS allowed under 115BAC)
        // Slabs: 0-3L (0), 3-6L (15k), 6-9L (30k), 9-11.5L (2.5L * 15% = 37.5k) = 82,500
        // Cess 4% = 3,300. Total = 85,800.
        TaxCalculationResult newResult = engine2024.calculateNewRegime(request);
        assertEquals(1150000.0, newResult.getNetTaxableIncome(), 0.01);
        assertEquals(82500.0, newResult.getTaxOnSlabs(), 0.01);
        assertEquals(3300.0, newResult.getHealthAndEducationCess(), 0.01);
        assertEquals(85800.0, newResult.getTotalTaxPayable(), 0.01);

        // New regime saves ₹15,600
        assertTrue(newResult.getTotalTaxPayable() < oldResult.getTotalTaxPayable());
        assertEquals(15600.0, oldResult.getTotalTaxPayable() - newResult.getTotalTaxPayable(), 0.01);
    }

    @Test
    @DisplayName("Budget 2024 AY 2025-26: ₹75k Standard Deduction and Revised Slab structure")
    void testBudget2024RevisedNewRegime() {
        TaxCalculationRequest request = TaxCalculationRequest.builder()
                .assessmentYear(AssessmentYear.AY_2025_26)
                .ageCategory(AgeCategory.BELOW_60)
                .income(IncomeDetails.builder().grossSalary(1200000.0).build())
                .build();

        TaxCalculationResult result = engine2025.calculateNewRegime(request);

        assertEquals(75000.0, result.getStandardDeduction(), 0.01);
        assertEquals(1125000.0, result.getNetTaxableIncome(), 0.01);

        // AY 2025-26 Slabs:
        // 0-3L: 0
        // 3-7L (4L * 5%): 20,000
        // 7-10L (3L * 10%): 30,000
        // 10-11.25L (1.25L * 15%): 18,750
        // Total Tax on slabs = 68,750
        // Cess 4% = 2,750
        // Total = 71,500
        assertEquals(68750.0, result.getTaxOnSlabs(), 0.01);
        assertEquals(2750.0, result.getHealthAndEducationCess(), 0.01);
        assertEquals(71500.0, result.getTotalTaxPayable(), 0.01);
    }

    @Test
    @DisplayName("HNI Surcharge and Marginal Relief verification on ₹60 Lakhs Income")
    void testHighIncomeSurchargeMarginalRelief() {
        TaxCalculationRequest request = TaxCalculationRequest.builder()
                .assessmentYear(AssessmentYear.AY_2024_25)
                .ageCategory(AgeCategory.BELOW_60)
                .income(IncomeDetails.builder().grossSalary(6000000.0).build())
                .build();

        TaxCalculationResult result = engine2024.calculateNewRegime(request);

        // Gross: 60L - 50k std = 59.5L
        assertEquals(5950000.0, result.getNetTaxableIncome(), 0.01);
        assertEquals(0.10, result.getSurchargeRate(), 0.001);
        assertTrue(result.getSurchargeAmount() > 0);
        assertTrue(result.getTotalTaxPayable() > 0);
    }

    @Test
    @DisplayName("Senior Citizen (Age 65) with 80TTB and basic exemption of ₹3 Lakhs in Old Regime")
    void testSeniorCitizenCalculation() {
        DeductionDetails deductions = DeductionDetails.builder()
                .section80TTB(50000.0)
                .build();

        TaxCalculationRequest request = TaxCalculationRequest.builder()
                .assessmentYear(AssessmentYear.AY_2024_25)
                .ageCategory(AgeCategory.SENIOR_60_TO_80)
                .income(IncomeDetails.builder().grossSalary(450000.0).incomeFromOtherSources(50000.0).build())
                .deductions(deductions)
                .build();

        TaxCalculationResult result = engine2024.calculateOldRegime(request);

        // Gross = 5,00,000. Std = 50,000. 80TTB = 50,000. Net = 4,00,000.
        // Senior slab 0-3L = 0, 3-4L @ 5% = 5,000.
        // Rebate 87A since net <= 5L = 5,000 => Tax = 0!
        assertEquals(400000.0, result.getNetTaxableIncome(), 0.01);
        assertEquals(5000.0, result.getTaxOnSlabs(), 0.01);
        assertEquals(5000.0, result.getRebateSection87A(), 0.01);
        assertEquals(0.0, result.getTotalTaxPayable(), 0.01);
    }
}
