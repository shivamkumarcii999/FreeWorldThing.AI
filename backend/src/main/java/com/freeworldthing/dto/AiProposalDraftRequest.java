package com.freeworldthing.dto;

public class AiProposalDraftRequest {
    private Long jobId;
    private Long freelancerId;
    private String customNote;

    public AiProposalDraftRequest() {}

    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }
    public Long getFreelancerId() { return freelancerId; }
    public void setFreelancerId(Long freelancerId) { this.freelancerId = freelancerId; }
    public String getCustomNote() { return customNote; }
    public void setCustomNote(String customNote) { this.customNote = customNote; }
}
