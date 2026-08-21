package com.cleartaxer.engine;

import com.cleartaxer.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DeductionCalculator {

    public static final double MAX_80C = 150000.0;
    public static final double MAX_80CCD_1B = 50000.0;
    public static final double MAX_80D_NON_SENIOR = 25000.0;
    public static final double MAX_80D_SENIOR = 50000.0;
    public static final double MAX_80TTA = 10000.0;
    public static final double MAX_80TTB = 50000.0;
    public static final double MAX_SEC_24B_LOSS = 200000.0;

    /**
     * Compute HRA Exemption under Section 10(13A) read with Rule 2A.
     */
    public double calculateHraExemption(HraDetails hra) {
        if (hra == null || hra.getHraReceived() <= 0 || hra.getRentPaid() <= 0) {
            return 0.0;
        }

        double salaryForHra = Math.max(0, hra.getBasicSalary() + hra.getDearnessAllowance());
        if (salaryForHra <= 0) {
            return 0.0;
        }

        // 1. Actual HRA received
        double actualHra = hra.getHraReceived();

        // 2. Rent paid excess of 10% of salary
        double rentExcess = Math.max(0, hra.getRentPaid() - (0.10 * salaryForHra));

        // 3. 50% of salary for Metro, 40% for Non-Metro
        double metroPercentage = (hra.getCityType() == MetroType.METRO) ? 0.50 : 0.40;
        double salaryPercentageCap = metroPercentage * salaryForHra;

        return Math.min(actualHra, Math.min(rentExcess, salaryPercentageCap));
    }

    /**
     * Compute Chapter VI-A deductions allowed under the Old Tax Regime.
     */
    public DeductionCalculationResult calculateOldRegimeDeductions(
            DeductionDetails deductions,
            HraDetails hra,
            AgeCategory ageCategory,
            double grossSalary,
            double otherIncome) {

        List<AuditTraceStep> auditSteps = new ArrayList<>();
        int step = 1;
        double totalDeductions = 0.0;

        // 1. HRA Exemption
        double hraExemption = 0.0;
        if (deductions.getCalculatedHraExemption() > 0) {
            hraExemption = deductions.getCalculatedHraExemption();
        } else if (hra != null) {
            hraExemption = calculateHraExemption(hra);
        }
        if (hraExemption > 0) {
            totalDeductions += hraExemption;
            auditSteps.add(AuditTraceStep.builder()
                    .stepNumber(step++)
                    .stepName("HRA Exemption")
                    .ruleReference("Section 10(13A) read with Rule 2A")
                    .description("Least of: Actual HRA, Rent - 10% Basic, or 40%/50% Basic")
                    .amount(hraExemption)
                    .formattedDetails(String.format("Exempted HRA: ₹%,.0f", hraExemption))
                    .build());
        }

        // 2. Section 80C
        double claimed80C = deductions.getSection80C();
        double allowed80C = Math.min(claimed80C, MAX_80C);
        if (allowed80C > 0) {
            totalDeductions += allowed80C;
            auditSteps.add(AuditTraceStep.builder()
                    .stepNumber(step++)
                    .stepName("Section 80C (Investments)")
                    .ruleReference("Section 80C")
                    .description("EPF, PPF, ELSS, Life Insurance, Home Loan Principal (Capped at ₹1.5L)")
                    .amount(allowed80C)
                    .formattedDetails(String.format("Claimed ₹%,.0f, Allowed ₹%,.0f", claimed80C, allowed80C))
                    .build());
        }

        // 3. Section 80D (Health Insurance)
        boolean isSelfSenior = (ageCategory == AgeCategory.SENIOR_60_TO_80 || ageCategory == AgeCategory.SUPER_SENIOR_ABOVE_80);
        double selfCap = isSelfSenior ? MAX_80D_SENIOR : MAX_80D_NON_SENIOR;
        double allowed80DSelf = Math.min(deductions.getSection80DSelf(), selfCap);

        double parentsCap = deductions.isParentsAreSeniorCitizens() ? MAX_80D_SENIOR : MAX_80D_NON_SENIOR;
        double allowed80DParents = Math.min(deductions.getSection80DParents(), parentsCap);

        double total80D = allowed80DSelf + allowed80DParents;
        if (total80D > 0) {
            totalDeductions += total80D;
            auditSteps.add(AuditTraceStep.builder()
                    .stepNumber(step++)
                    .stepName("Section 80D (Health Insurance)")
                    .ruleReference("Section 80D")
                    .description(String.format("Self/Family: ₹%,.0f (Cap ₹%,.0f) + Parents: ₹%,.0f (Cap ₹%,.0f)",
                            allowed80DSelf, selfCap, allowed80DParents, parentsCap))
                    .amount(total80D)
                    .formattedDetails(String.format("Total 80D deduction: ₹%,.0f", total80D))
                    .build());
        }

        // 4. Section 80CCD(1B) (NPS Employee voluntary)
        double allowed80CCD1B = Math.min(deductions.getSection80CCD1B(), MAX_80CCD_1B);
        if (allowed80CCD1B > 0) {
            totalDeductions += allowed80CCD1B;
            auditSteps.add(AuditTraceStep.builder()
                    .stepNumber(step++)
                    .stepName("Section 80CCD(1B) (NPS Tier-1)")
                    .ruleReference("Section 80CCD(1B)")
                    .description("Exclusive additional deduction for National Pension System up to ₹50,000")
                    .amount(allowed80CCD1B)
                    .formattedDetails(String.format("Allowed ₹%,.0f", allowed80CCD1B))
                    .build());
        }

        // 5. Section 80CCD(2) (Employer NPS Contribution)
        if (deductions.getSection80CCD2() > 0) {
            totalDeductions += deductions.getSection80CCD2();
            auditSteps.add(AuditTraceStep.builder()
                    .stepNumber(step++)
                    .stepName("Section 80CCD(2) (Employer NPS)")
                    .ruleReference("Section 80CCD(2)")
                    .description("Employer's contribution to NPS (Allowed in both Old & New Regimes)")
                    .amount(deductions.getSection80CCD2())
                    .formattedDetails(String.format("Allowed ₹%,.0f", deductions.getSection80CCD2()))
                    .build());
        }

        // 6. Section 80E (Education loan interest)
        if (deductions.getSection80E() > 0) {
            totalDeductions += deductions.getSection80E();
            auditSteps.add(AuditTraceStep.builder()
                    .stepNumber(step++)
                    .stepName("Section 80E (Education Loan Interest)")
                    .ruleReference("Section 80E")
                    .description("Full interest paid on loan for higher education")
                    .amount(deductions.getSection80E())
                    .formattedDetails(String.format("Allowed ₹%,.0f", deductions.getSection80E()))
                    .build());
        }

        // 7. Section 80G (Charitable donations)
        if (deductions.getSection80G() > 0) {
            totalDeductions += deductions.getSection80G();
            auditSteps.add(AuditTraceStep.builder()
                    .stepNumber(step++)
                    .stepName("Section 80G (Donations)")
                    .ruleReference("Section 80G")
                    .description("Donations to eligible charitable institutions and relief funds")
                    .amount(deductions.getSection80G())
                    .formattedDetails(String.format("Allowed ₹%,.0f", deductions.getSection80G()))
                    .build());
        }

        // 8. Section 80TTA / 80TTB (Interest income)
        if (isSelfSenior) {
            double allowed80TTB = Math.min(deductions.getSection80TTB() > 0 ? deductions.getSection80TTB() : otherIncome, MAX_80TTB);
            if (allowed80TTB > 0) {
                totalDeductions += allowed80TTB;
                auditSteps.add(AuditTraceStep.builder()
                        .stepNumber(step++)
                        .stepName("Section 80TTB (Senior Interest)")
                        .ruleReference("Section 80TTB")
                        .description("Interest from savings & deposits for senior citizens up to ₹50,000")
                        .amount(allowed80TTB)
                        .formattedDetails(String.format("Allowed ₹%,.0f", allowed80TTB))
                        .build());
            }
        } else {
            double allowed80TTA = Math.min(deductions.getSection80TTA() > 0 ? deductions.getSection80TTA() : otherIncome, MAX_80TTA);
            if (allowed80TTA > 0) {
                totalDeductions += allowed80TTA;
                auditSteps.add(AuditTraceStep.builder()
                        .stepNumber(step++)
                        .stepName("Section 80TTA (Savings Interest)")
                        .ruleReference("Section 80TTA")
                        .description("Interest on savings bank accounts up to ₹10,000")
                        .amount(allowed80TTA)
                        .formattedDetails(String.format("Allowed ₹%,.0f", allowed80TTA))
                        .build());
            }
        }

        // 9. Other deductions
        if (deductions.getOtherDeductions() > 0) {
            totalDeductions += deductions.getOtherDeductions();
            auditSteps.add(AuditTraceStep.builder()
                    .stepNumber(step++)
                    .stepName("Other Chapter VI-A Deductions")
                    .ruleReference("Chapter VI-A")
                    .description("Miscellaneous eligible tax deductions")
                    .amount(deductions.getOtherDeductions())
                    .formattedDetails(String.format("Allowed ₹%,.0f", deductions.getOtherDeductions()))
                    .build());
        }

        return new DeductionCalculationResult(totalDeductions, hraExemption, auditSteps);
    }

    /**
     * Compute deductions allowed under New Tax Regime (Section 115BAC).
     * Only Section 80CCD(2) (Employer NPS) is permitted under New Regime.
     */
    public DeductionCalculationResult calculateNewRegimeDeductions(DeductionDetails deductions) {
        List<AuditTraceStep> auditSteps = new ArrayList<>();
        double total = 0.0;

        if (deductions != null && deductions.getSection80CCD2() > 0) {
            total += deductions.getSection80CCD2();
            auditSteps.add(AuditTraceStep.builder()
                    .stepNumber(1)
                    .stepName("Section 80CCD(2) (Employer NPS)")
                    .ruleReference("Section 80CCD(2) / 115BAC(1A)")
                    .description("Employer's contribution to NPS is permissible under Section 115BAC")
                    .amount(deductions.getSection80CCD2())
                    .formattedDetails(String.format("Allowed ₹%,.0f", deductions.getSection80CCD2()))
                    .build());
        }

        return new DeductionCalculationResult(total, 0.0, auditSteps);
    }

    public record DeductionCalculationResult(
            double totalDeductions,
            double hraExemption,
            List<AuditTraceStep> auditSteps
    ) {}
}
