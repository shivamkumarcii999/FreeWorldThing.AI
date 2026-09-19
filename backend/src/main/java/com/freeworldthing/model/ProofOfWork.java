package com.freeworldthing.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "proof_of_work")
public class ProofOfWork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String projectTitle;

    @Column(length = 2000)
    private String problem;

    @Column(length = 2000)
    private String solution;

    private String technologies;
    private String repoUrl;
    private String liveDemoUrl;
    private Double evidenceScore;
    private Integer testCoveragePercent;
    private Integer commitCount;
    private Boolean verified = true;
    private LocalDateTime verifiedAt;

    public ProofOfWork() {}

    @PrePersist
    protected void onCreate() {
        if (this.verified == null) {
            this.verified = true;
        }
        if (this.verifiedAt == null) {
            this.verifiedAt = LocalDateTime.now();
        }
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private ProofOfWork pow = new ProofOfWork();
        public Builder id(Long id) { pow.id = id; return this; }
        public Builder userId(Long userId) { pow.userId = userId; return this; }
        public Builder projectTitle(String projectTitle) { pow.projectTitle = projectTitle; return this; }
        public Builder problem(String problem) { pow.problem = problem; return this; }
        public Builder solution(String solution) { pow.solution = solution; return this; }
        public Builder technologies(String technologies) { pow.technologies = technologies; return this; }
        public Builder repoUrl(String repoUrl) { pow.repoUrl = repoUrl; return this; }
        public Builder liveDemoUrl(String liveDemoUrl) { pow.liveDemoUrl = liveDemoUrl; return this; }
        public Builder evidenceScore(Double evidenceScore) { pow.evidenceScore = evidenceScore; return this; }
        public Builder testCoveragePercent(Integer testCoveragePercent) { pow.testCoveragePercent = testCoveragePercent; return this; }
        public Builder commitCount(Integer commitCount) { pow.commitCount = commitCount; return this; }
        public Builder verified(Boolean verified) { pow.verified = verified; return this; }
        public Builder verifiedAt(LocalDateTime verifiedAt) { pow.verifiedAt = verifiedAt; return this; }
        public ProofOfWork build() { return pow; }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getProjectTitle() { return projectTitle; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }
    public String getProblem() { return problem; }
    public void setProblem(String problem) { this.problem = problem; }
    public String getSolution() { return solution; }
    public void setSolution(String solution) { this.solution = solution; }
    public String getTechnologies() { return technologies; }
    public void setTechnologies(String technologies) { this.technologies = technologies; }
    public String getRepoUrl() { return repoUrl; }
    public void setRepoUrl(String repoUrl) { this.repoUrl = repoUrl; }
    public String getLiveDemoUrl() { return liveDemoUrl; }
    public void setLiveDemoUrl(String liveDemoUrl) { this.liveDemoUrl = liveDemoUrl; }
    public Double getEvidenceScore() { return evidenceScore; }
    public void setEvidenceScore(Double evidenceScore) { this.evidenceScore = evidenceScore; }
    public Integer getTestCoveragePercent() { return testCoveragePercent; }
    public void setTestCoveragePercent(Integer testCoveragePercent) { this.testCoveragePercent = testCoveragePercent; }
    public Integer getCommitCount() { return commitCount; }
    public void setCommitCount(Integer commitCount) { this.commitCount = commitCount; }
    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }
    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }
}
