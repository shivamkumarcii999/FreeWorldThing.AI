package com.freeworldthing.dto;

import java.util.List;

public class AiJobSpecResponse {
    private String title;
    private String description;
    private String category;
    private List<String> requiredSkills;
    private Double minBudget;
    private Double maxBudget;
    private String budgetCurrency;
    private Integer estimatedDurationWeeks;
    private String complexity;
    private String projectType;
    private List<SuggestedMilestone> suggestedMilestones;
    private Double aiConfidenceScore;

    public AiJobSpecResponse() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private AiJobSpecResponse r = new AiJobSpecResponse();
        public Builder title(String t) { r.title = t; return this; }
        public Builder description(String d) { r.description = d; return this; }
        public Builder category(String c) { r.category = c; return this; }
        public Builder requiredSkills(List<String> s) { r.requiredSkills = s; return this; }
        public Builder minBudget(Double b) { r.minBudget = b; return this; }
        public Builder maxBudget(Double b) { r.maxBudget = b; return this; }
        public Builder budgetCurrency(String c) { r.budgetCurrency = c; return this; }
        public Builder estimatedDurationWeeks(Integer d) { r.estimatedDurationWeeks = d; return this; }
        public Builder complexity(String c) { r.complexity = c; return this; }
        public Builder projectType(String p) { r.projectType = p; return this; }
        public Builder suggestedMilestones(List<SuggestedMilestone> m) { r.suggestedMilestones = m; return this; }
        public Builder aiConfidenceScore(Double s) { r.aiConfidenceScore = s; return this; }
        public AiJobSpecResponse build() { return r; }
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public List<String> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills; }
    public Double getMinBudget() { return minBudget; }
    public void setMinBudget(Double minBudget) { this.minBudget = minBudget; }
    public Double getMaxBudget() { return maxBudget; }
    public void setMaxBudget(Double maxBudget) { this.maxBudget = maxBudget; }
    public String getBudgetCurrency() { return budgetCurrency; }
    public void setBudgetCurrency(String budgetCurrency) { this.budgetCurrency = budgetCurrency; }
    public Integer getEstimatedDurationWeeks() { return estimatedDurationWeeks; }
    public void setEstimatedDurationWeeks(Integer estimatedDurationWeeks) { this.estimatedDurationWeeks = estimatedDurationWeeks; }
    public String getComplexity() { return complexity; }
    public void setComplexity(String complexity) { this.complexity = complexity; }
    public String getProjectType() { return projectType; }
    public void setProjectType(String projectType) { this.projectType = projectType; }
    public List<SuggestedMilestone> getSuggestedMilestones() { return suggestedMilestones; }
    public void setSuggestedMilestones(List<SuggestedMilestone> suggestedMilestones) { this.suggestedMilestones = suggestedMilestones; }
    public Double getAiConfidenceScore() { return aiConfidenceScore; }
    public void setAiConfidenceScore(Double aiConfidenceScore) { this.aiConfidenceScore = aiConfidenceScore; }
}
