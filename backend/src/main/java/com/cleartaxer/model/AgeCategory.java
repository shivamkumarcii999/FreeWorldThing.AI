package com.cleartaxer.model;

public enum AgeCategory {
    BELOW_60("Individual (< 60 years)"),
    SENIOR_60_TO_80("Senior Citizen (60 - 80 years)"),
    SUPER_SENIOR_ABOVE_80("Super Senior Citizen (> 80 years)");

    private final String description;

    AgeCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
