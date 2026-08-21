package com.cleartaxer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxExplanationResponse {
    private String executiveSummary;
    private String plainLanguageBreakdown;
    @Builder.Default
    private List<String> keyObservations = new ArrayList<>();
    @Builder.Default
    private List<String> strategicTips = new ArrayList<>();
    @Builder.Default
    private List<String> faqClarifications = new ArrayList<>();
    private String statutoryDisclaimer;
}
