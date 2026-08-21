package com.cleartaxer.engine;

import com.cleartaxer.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PresumptiveTaxEngineTest {

    private PresumptiveTaxEngine presumptiveTaxEngine;

    @BeforeEach
    void setUp() {
        DeductionCalculator deductionCalculator = new DeductionCalculator();
        AY2024_25_RuleEngine engine2024 = new AY2024_25_RuleEngine(deductionCalculator);
        AY2025_26_RuleEngine engine2025 = new AY2025_26_RuleEngine(deductionCalculator);
        RuleEngineRegistry registry = new RuleEngineRegistry(List.of(engine2024, engine2025));
        presumptiveTaxEngine = new PresumptiveTaxEngine(registry);
    }

    @Test
    @DisplayName("Section 44ADA: Freelance Software Engineer with ₹30L digital receipts (50% deemed profit)")
    void testFreelancerSection44ADA() {
        PresumptiveTaxRequest request = PresumptiveTaxRequest.builder()
                .assessmentYear(AssessmentYear.AY_2024_25)
                .section(PresumptiveSection.SECTION_44ADA)
                .digitalReceipts(3000000.0)
                .cashReceipts(0.0)
                .build();

        PresumptiveTaxResult result = presumptiveTaxEngine.calculate(request);

        assertTrue(result.isEligible());
        assertEquals(7500000.0, result.getEligibilityThreshold(), 0.01); // 95%+ digital => 75L limit
        assertEquals(50.0, result.getDeemedProfitRate(), 0.01);
        assertEquals(1500000.0, result.getDeemedProfitAmount(), 0.01);

        // Advance tax schedule should have 4 entries with 100% on 15th March
        assertEquals(4, result.getAdvanceTaxSchedule().size());
        assertTrue(result.getTaxPayable() > 0);

        // GST threshold (20L for services) exceeded
        assertTrue(result.isGstRegistrationMandatory());
    }

    @Test
    @DisplayName("Section 44AD: Small Business Retailer with ₹80L digital turnover (6% deemed profit)")
    void testSmallBusinessSection44AD() {
        PresumptiveTaxRequest request = PresumptiveTaxRequest.builder()
                .assessmentYear(AssessmentYear.AY_2024_25)
                .section(PresumptiveSection.SECTION_44AD)
                .digitalReceipts(8000000.0)
                .cashReceipts(0.0)
                .build();

        PresumptiveTaxResult result = presumptiveTaxEngine.calculate(request);

        assertTrue(result.isEligible());
        assertEquals(30000000.0, result.getEligibilityThreshold(), 0.01); // 3 Cr limit
        assertEquals(6.0, result.getDeemedProfitRate(), 0.01);
        assertEquals(480000.0, result.getDeemedProfitAmount(), 0.01); // 6% of 80L = 4.8L

        // Since taxable income is 4.8L <= 7.0L, New Regime gives 100% rebate under 87A (Tax = 0)
        assertEquals(0.0, result.getNewRegimeCalculation().getTotalTaxPayable(), 0.01);
        assertEquals(TaxRegime.NEW, result.getRecommendedRegime());
    }
}
