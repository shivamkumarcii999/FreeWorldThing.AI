package com.freeworldthing.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "milestones")
public class Milestone {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contract_id")
    private Contract contract;

    private Integer sequence;
    private String title;
    @Column(length = 2000)
    private String description;

    private BigDecimal amount;
    private Instant dueDate;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @Column(length = 4000)
    private String deliverableNotes;

    private Instant submittedAt;
    private Instant approvedAt;
    private Instant paidAt;

    public enum Status { PENDING, FUNDED, SUBMITTED, APPROVED, PAID, DISPUTED }

    public Milestone() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Milestone m = new Milestone();
        public Builder id(Long id) { m.id = id; return this; }
        public Builder contract(Contract contract) { m.contract = contract; return this; }
        public Builder sequence(Integer sequence) { m.sequence = sequence; return this; }
        public Builder title(String title) { m.title = title; return this; }
        public Builder description(String description) { m.description = description; return this; }
        public Builder amount(BigDecimal amount) { m.amount = amount; return this; }
        public Builder dueDate(Instant dueDate) { m.dueDate = dueDate; return this; }
        public Builder status(Status status) { m.status = status; return this; }
        public Builder deliverableNotes(String deliverableNotes) { m.deliverableNotes = deliverableNotes; return this; }
        public Builder submittedAt(Instant submittedAt) { m.submittedAt = submittedAt; return this; }
        public Builder approvedAt(Instant approvedAt) { m.approvedAt = approvedAt; return this; }
        public Builder paidAt(Instant paidAt) { m.paidAt = paidAt; return this; }
        public Milestone build() { return m; }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Contract getContract() { return contract; }
    public void setContract(Contract contract) { this.contract = contract; }
    public Integer getSequence() { return sequence; }
    public void setSequence(Integer sequence) { this.sequence = sequence; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Instant getDueDate() { return dueDate; }
    public void setDueDate(Instant dueDate) { this.dueDate = dueDate; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getDeliverableNotes() { return deliverableNotes; }
    public void setDeliverableNotes(String deliverableNotes) { this.deliverableNotes = deliverableNotes; }
    public Instant getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Instant submittedAt) { this.submittedAt = submittedAt; }
    public Instant getApprovedAt() { return approvedAt; }
    public void setApprovedAt(Instant approvedAt) { this.approvedAt = approvedAt; }
    public Instant getPaidAt() { return paidAt; }
    public void setPaidAt(Instant paidAt) { this.paidAt = paidAt; }
}
