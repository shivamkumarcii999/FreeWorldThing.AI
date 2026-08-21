package com.cleartaxer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeductionDetails {
    /** Section 80C: EPF, PPF, ELSS, Life Insurance, Tuition Fees, Principal Home Loan (Capped at 1.5 Lakhs) */
    @Builder.Default
    private double section80C = 0.0;

    /** Section 80D: Self, Spouse, Children health insurance (Up to 25k, or 50k if senior) */
    @Builder.Default
    private double section80DSelf = 0.0;

    /** Section 80D: Parents health insurance (Up to 25k, or 50k if senior) */
    @Builder.Default
    private double section80DParents = 0.0;

    /** Are parents senior citizens (>= 60 years)? */
    @Builder.Default
    private boolean parentsAreSeniorCitizens = false;

    /** Section 80CCD(1B): Additional NPS contribution (Capped at 50,000) */
    @Builder.Default
    private double section80CCD1B = 0.0;

    /** Section 80CCD(2): Employer NPS contribution (Allowed in BOTH Old and New Regime up to 10%/14% of Basic) */
    @Builder.Default
    private double section80CCD2 = 0.0;

    /** Section 80E: Interest on Education Loan (No upper limit) */
    @Builder.Default
    private double section80E = 0.0;

    /** Section 80G: Donations to approved charitable funds */
    @Builder.Default
    private double section80G = 0.0;

    /** Section 80TTA: Interest on Savings Bank Accounts (Max 10,000 for non-seniors) */
    @Builder.Default
    private double section80TTA = 0.0;

    /** Section 80TTB: Interest from deposits for senior citizens (Max 50,000) */
    @Builder.Default
    private double section80TTB = 0.0;

    /** Section 24(b): Loss from Self-Occupied House Property (Max 2,00,000 in Old Regime) */
    @Builder.Default
    private double homeLoanInterestSelfOccupied = 0.0;

    /** Direct HRA exemption amount if already calculated */
    @Builder.Default
    private double calculatedHraExemption = 0.0;

    /** Other Chapter VI-A deductions */
    @Builder.Default
    private double otherDeductions = 0.0;
}
