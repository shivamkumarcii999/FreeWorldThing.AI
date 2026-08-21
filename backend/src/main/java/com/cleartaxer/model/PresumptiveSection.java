package com.cleartaxer.model;

public enum PresumptiveSection {
    SECTION_44ADA("Section 44ADA (Professionals - Tech, Medical, Legal, Accountancy, Architecture)"),
    SECTION_44AD("Section 44AD (Small Business / Retail / Trading / Manufacturing)");

    private final String title;

    PresumptiveSection(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
