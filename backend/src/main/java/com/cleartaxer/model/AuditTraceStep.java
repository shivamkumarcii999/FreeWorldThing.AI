package com.cleartaxer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditTraceStep {
    private int stepNumber;
    private String stepName;
    private String ruleReference;
    private String description;
    private double amount;
    private String formattedDetails;
}
