package com.freeworldthing.dto;

import java.util.List;

public class AiProjectHealthReport {
    private String healthStatus;
    private String executiveSummary;
    private Integer completionPercent;
    private List<String> blockers;
    private List<String> nextSteps;
    private Double confidenceScore;

    public AiProjectHealthReport() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private AiProjectHealthReport r = new AiProjectHealthReport();
        public Builder healthStatus(String s) { r.healthStatus = s; return this; }
        public Builder executiveSummary(String s) { r.executiveSummary = s; return this; }
        public Builder completionPercent(Integer p) { r.completionPercent = p; return this; }
        public Builder blockers(List<String> b) { r.blockers = b; return this; }
        public Builder nextSteps(List<String> n) { r.nextSteps = n; return this; }
        public Builder confidenceScore(Double c) { r.confidenceScore = c; return this; }
        public AiProjectHealthReport build() { return r; }
    }

    public String getHealthStatus() { return healthStatus; }
    public void setHealthStatus(String healthStatus) { this.healthStatus = healthStatus; }
    public String getExecutiveSummary() { return executiveSummary; }
    public void setExecutiveSummary(String executiveSummary) { this.executiveSummary = executiveSummary; }
    public Integer getCompletionPercent() { return completionPercent; }
    public void setCompletionPercent(Integer completionPercent) { this.completionPercent = completionPercent; }
    public List<String> getBlockers() { return blockers; }
    public void setBlockers(List<String> blockers) { this.blockers = blockers; }
    public List<String> getNextSteps() { return nextSteps; }
    public void setNextSteps(List<String> nextSteps) { this.nextSteps = nextSteps; }
    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }
}
