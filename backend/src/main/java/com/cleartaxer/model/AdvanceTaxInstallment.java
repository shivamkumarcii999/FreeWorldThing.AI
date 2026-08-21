package com.cleartaxer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvanceTaxInstallment {
    private int installmentNumber;
    private String dueDate;
    private double cumulativePercentage;
    private double cumulativeAmountDue;
    private double installmentAmount;
    private String note;
}
