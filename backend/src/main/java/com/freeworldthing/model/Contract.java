package com.freeworldthing.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contracts")
public class Contract {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_id")
    private Job job; // null for direct hires and service purchases

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "client_id")
    private User client;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "freelancer_id")
    private User freelancer;

    @Column(nullable = false)
    private String title;

    private BigDecimal totalValue;
    private String currency = "INR";

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    private BigDecimal fundedAmount = BigDecimal.ZERO;
    private BigDecimal releasedAmount = BigDecimal.ZERO;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("sequence asc")
    private List<Milestone> milestones = new ArrayList<>();

    @CreationTimestamp
    private Instant createdAt;

    public enum Status { ACTIVE, COMPLETED, CANCELLED, DISPUTED }

    public Contract() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Contract c = new Contract();
        public Builder id(Long id) { c.id = id; return this; }
        public Builder job(Job job) { c.job = job; return this; }
        public Builder client(User client) { c.client = client; return this; }
        public Builder freelancer(User freelancer) { c.freelancer = freelancer; return this; }
        public Builder title(String title) { c.title = title; return this; }
        public Builder totalValue(BigDecimal totalValue) { c.totalValue = totalValue; return this; }
        public Builder currency(String currency) { c.currency = currency; return this; }
        public Builder status(Status status) { c.status = status; return this; }
        public Builder fundedAmount(BigDecimal fundedAmount) { c.fundedAmount = fundedAmount; return this; }
        public Builder releasedAmount(BigDecimal releasedAmount) { c.releasedAmount = releasedAmount; return this; }
        public Builder milestones(List<Milestone> milestones) { c.milestones = milestones; return this; }
        public Contract build() { return c; }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }
    public User getClient() { return client; }
    public void setClient(User client) { this.client = client; }
    public User getFreelancer() { return freelancer; }
    public void setFreelancer(User freelancer) { this.freelancer = freelancer; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public BigDecimal getFundedAmount() { return fundedAmount; }
    public void setFundedAmount(BigDecimal fundedAmount) { this.fundedAmount = fundedAmount; }
    public BigDecimal getReleasedAmount() { return releasedAmount; }
    public void setReleasedAmount(BigDecimal releasedAmount) { this.releasedAmount = releasedAmount; }
    public List<Milestone> getMilestones() { return milestones; }
    public void setMilestones(List<Milestone> milestones) { this.milestones = milestones; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
