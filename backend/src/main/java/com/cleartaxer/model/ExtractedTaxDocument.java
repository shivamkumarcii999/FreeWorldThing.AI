package com.cleartaxer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtractedTaxDocument {
    private String documentType; // Form 16, Form 26AS, Salary Slip, Interest Certificate
    private String employerName;
    private String panNumber;
    private String assessmentYear;

    private double grossSalary;
    private double hraReceived;
    private double basicSalary;
    private double standardDeductionClaimed;

    private double section80C;
    private double section80D;
    private double section80CCD1B;
    private double section80CCD2;
    private double otherDeductions;

    private double tdsDeducted;
    private double interestIncome;

    /** Field-level confidence scores (0.0 to 1.0) */
    @Builder.Default
    private Map<String, Double> fieldConfidence = new HashMap<>();

    /** Flags requiring user attention */
    private String verificationNotes;
}
