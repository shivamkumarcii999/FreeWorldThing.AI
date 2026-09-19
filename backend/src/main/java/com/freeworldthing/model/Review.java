package com.freeworldthing.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "reviews", uniqueConstraints = @UniqueConstraint(columnNames = {"contract_id", "author_id"}))
public class Review {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "subject_id")
    private User subject;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Direction direction;

    private int overall;

    private Integer quality;
    private Integer communication;
    private Integer reliability;
    private Integer deadlineAdherence;

    private Integer requirementClarity;
    private Integer paymentBehavior;
    private Integer scopeStability;

    @Column(length = 4000)
    private String comment;

    @CreationTimestamp
    private Instant createdAt;

    public enum Direction { CLIENT_TO_FREELANCER, FREELANCER_TO_CLIENT }

    public Review() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Review r = new Review();
        public Builder id(Long id) { r.id = id; return this; }
        public Builder contract(Contract contract) { r.contract = contract; return this; }
        public Builder author(User author) { r.author = author; return this; }
        public Builder subject(User subject) { r.subject = subject; return this; }
        public Builder direction(Direction direction) { r.direction = direction; return this; }
        public Builder overall(int overall) { r.overall = overall; return this; }
        public Builder quality(Integer quality) { r.quality = quality; return this; }
        public Builder communication(Integer communication) { r.communication = communication; return this; }
        public Builder reliability(Integer reliability) { r.reliability = reliability; return this; }
        public Builder deadlineAdherence(Integer deadlineAdherence) { r.deadlineAdherence = deadlineAdherence; return this; }
        public Builder requirementClarity(Integer rc) { r.requirementClarity = rc; return this; }
        public Builder paymentBehavior(Integer pb) { r.paymentBehavior = pb; return this; }
        public Builder scopeStability(Integer ss) { r.scopeStability = ss; return this; }
        public Builder comment(String comment) { r.comment = comment; return this; }
        public Review build() { return r; }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Contract getContract() { return contract; }
    public void setContract(Contract contract) { this.contract = contract; }
    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
    public User getSubject() { return subject; }
    public void setSubject(User subject) { this.subject = subject; }
    public Direction getDirection() { return direction; }
    public void setDirection(Direction direction) { this.direction = direction; }
    public int getOverall() { return overall; }
    public void setOverall(int overall) { this.overall = overall; }
    public Integer getQuality() { return quality; }
    public void setQuality(Integer quality) { this.quality = quality; }
    public Integer getCommunication() { return communication; }
    public void setCommunication(Integer communication) { this.communication = communication; }
    public Integer getReliability() { return reliability; }
    public void setReliability(Integer reliability) { this.reliability = reliability; }
    public Integer getDeadlineAdherence() { return deadlineAdherence; }
    public void setDeadlineAdherence(Integer deadlineAdherence) { this.deadlineAdherence = deadlineAdherence; }
    public Integer getRequirementClarity() { return requirementClarity; }
    public void setRequirementClarity(Integer requirementClarity) { this.requirementClarity = requirementClarity; }
    public Integer getPaymentBehavior() { return paymentBehavior; }
    public void setPaymentBehavior(Integer paymentBehavior) { this.paymentBehavior = paymentBehavior; }
    public Integer getScopeStability() { return scopeStability; }
    public void setScopeStability(Integer scopeStability) { this.scopeStability = scopeStability; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
