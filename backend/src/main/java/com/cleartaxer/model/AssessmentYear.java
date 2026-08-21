package com.cleartaxer.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AssessmentYear {
    AY_2024_25("2024-25", "FY 2023-24"),
    AY_2025_26("2025-26", "FY 2024-25 (Budget 2024)");

    private final String label;
    private final String financialYear;

    AssessmentYear(String label, String financialYear) {
        this.label = label;
        this.financialYear = financialYear;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    public String getFinancialYear() {
        return financialYear;
    }

    public static AssessmentYear fromString(String text) {
        if (text == null) return AY_2024_25;
        for (AssessmentYear ay : AssessmentYear.values()) {
            if (ay.label.equalsIgnoreCase(text.trim()) || ay.name().equalsIgnoreCase(text.trim())) {
                return ay;
            }
        }
        return AY_2024_25;
    }
}
