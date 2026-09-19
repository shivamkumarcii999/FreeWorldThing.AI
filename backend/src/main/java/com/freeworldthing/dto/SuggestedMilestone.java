package com.freeworldthing.dto;

public class SuggestedMilestone {
    private Integer sequenceOrder;
    private String title;
    private String description;
    private Double suggestedAmount;
    private Integer estimatedDays;

    public SuggestedMilestone() {}

    public SuggestedMilestone(Integer sequenceOrder, String title, String description, Double suggestedAmount, Integer estimatedDays) {
        this.sequenceOrder = sequenceOrder;
        this.title = title;
        this.description = description;
        this.suggestedAmount = suggestedAmount;
        this.estimatedDays = estimatedDays;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private SuggestedMilestone sm = new SuggestedMilestone();
        public Builder sequenceOrder(Integer s) { sm.sequenceOrder = s; return this; }
        public Builder title(String t) { sm.title = t; return this; }
        public Builder description(String d) { sm.description = d; return this; }
        public Builder suggestedAmount(Double a) { sm.suggestedAmount = a; return this; }
        public Builder estimatedDays(Integer e) { sm.estimatedDays = e; return this; }
        public SuggestedMilestone build() { return sm; }
    }

    public Integer getSequenceOrder() { return sequenceOrder; }
    public void setSequenceOrder(Integer sequenceOrder) { this.sequenceOrder = sequenceOrder; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getSuggestedAmount() { return suggestedAmount; }
    public void setSuggestedAmount(Double suggestedAmount) { this.suggestedAmount = suggestedAmount; }
    public Integer getEstimatedDays() { return estimatedDays; }
    public void setEstimatedDays(Integer estimatedDays) { this.estimatedDays = estimatedDays; }
}
