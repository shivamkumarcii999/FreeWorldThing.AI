package com.cleartaxer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxExplanationRequest {
    private TaxCalculationResult calculationResult;
    private RegimeComparisonResult comparisonResult;
    private String userQuestion; // e.g. "Why is new regime better?", "What if I invest 50k more in NPS?"
}
