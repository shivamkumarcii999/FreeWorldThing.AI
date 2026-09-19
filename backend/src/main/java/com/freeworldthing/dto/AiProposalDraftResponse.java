package com.freeworldthing.dto;

import java.util.List;

public class AiProposalDraftResponse {
    private String coverLetter;
    private Double suggestedBudget;
    private Integer suggestedDurationDays;
    private List<String> highlightedProofs;
    private Double qualityScore;
    private List<String> recommendations;

    public AiProposalDraftResponse() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private AiProposalDraftResponse r = new AiProposalDraftResponse();
        public Builder coverLetter(String c) { r.coverLetter = c; return this; }
        public Builder suggestedBudget(Double b) { r.suggestedBudget = b; return this; }
        public Builder suggestedDurationDays(Integer d) { r.suggestedDurationDays = d; return this; }
        public Builder highlightedProofs(List<String> p) { r.highlightedProofs = p; return this; }
        public Builder qualityScore(Double s) { r.qualityScore = s; return this; }
        public Builder recommendations(List<String> recs) { r.recommendations = recs; return this; }
        public AiProposalDraftResponse build() { return r; }
    }

    public String getCoverLetter() { return coverLetter; }
    public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }
    public Double getSuggestedBudget() { return suggestedBudget; }
    public void setSuggestedBudget(Double suggestedBudget) { this.suggestedBudget = suggestedBudget; }
    public Integer getSuggestedDurationDays() { return suggestedDurationDays; }
    public void setSuggestedDurationDays(Integer suggestedDurationDays) { this.suggestedDurationDays = suggestedDurationDays; }
    public List<String> getHighlightedProofs() { return highlightedProofs; }
    public void setHighlightedProofs(List<String> highlightedProofs) { this.highlightedProofs = highlightedProofs; }
    public Double getQualityScore() { return qualityScore; }
    public void setQualityScore(Double qualityScore) { this.qualityScore = qualityScore; }
    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
}
