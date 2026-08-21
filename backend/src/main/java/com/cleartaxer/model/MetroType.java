package com.cleartaxer.model;

public enum MetroType {
    METRO("Metro (Delhi, Mumbai, Kolkata, Chennai - 50% of Basic)"),
    NON_METRO("Non-Metro (40% of Basic)");

    private final String description;

    MetroType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
