package com.freeworldthing.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "proposals", uniqueConstraints = @UniqueConstraint(columnNames = {"job_id", "freelancer_id"}))
public class Proposal {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "job_id")
    private Job job;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "freelancer_id")
    private User freelancer;

    @Column(nullable = false, length = 8000)
    private String coverLetter;

    private BigDecimal bidAmount;
    private Integer durationWeeks;

    @Enumerated(EnumType.STRING)
    private Status status = Status.SUBMITTED;

    private Integer aiQualityScore;
    private Integer matchScore;
    private boolean aiDrafted;

    @CreationTimestamp
    private Instant createdAt;

    public enum Status { SUBMITTED, SHORTLISTED, REJECTED, ACCEPTED, WITHDRAWN }

    public Proposal() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Proposal p = new Proposal();
        public Builder id(Long id) { p.id = id; return this; }
        public Builder job(Job job) { p.job = job; return this; }
        public Builder freelancer(User freelancer) { p.freelancer = freelancer; return this; }
        public Builder coverLetter(String coverLetter) { p.coverLetter = coverLetter; return this; }
        public Builder bidAmount(BigDecimal bidAmount) { p.bidAmount = bidAmount; return this; }
        public Builder durationWeeks(Integer durationWeeks) { p.durationWeeks = durationWeeks; return this; }
        public Builder status(Status status) { p.status = status; return this; }
        public Builder aiQualityScore(Integer aiQualityScore) { p.aiQualityScore = aiQualityScore; return this; }
        public Builder matchScore(Integer matchScore) { p.matchScore = matchScore; return this; }
        public Builder aiDrafted(boolean aiDrafted) { p.aiDrafted = aiDrafted; return this; }
        public Proposal build() { return p; }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }
    public User getFreelancer() { return freelancer; }
    public void setFreelancer(User freelancer) { this.freelancer = freelancer; }
    public String getCoverLetter() { return coverLetter; }
    public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }
    public BigDecimal getBidAmount() { return bidAmount; }
    public void setBidAmount(BigDecimal bidAmount) { this.bidAmount = bidAmount; }
    public Integer getDurationWeeks() { return durationWeeks; }
    public void setDurationWeeks(Integer durationWeeks) { this.durationWeeks = durationWeeks; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Integer getAiQualityScore() { return aiQualityScore; }
    public void setAiQualityScore(Integer aiQualityScore) { this.aiQualityScore = aiQualityScore; }
    public Integer getMatchScore() { return matchScore; }
    public void setMatchScore(Integer matchScore) { this.matchScore = matchScore; }
    public boolean isAiDrafted() { return aiDrafted; }
    public void setAiDrafted(boolean aiDrafted) { this.aiDrafted = aiDrafted; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
