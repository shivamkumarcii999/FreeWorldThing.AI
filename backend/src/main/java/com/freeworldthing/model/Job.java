package com.freeworldthing.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "jobs")
public class Job {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "client_id")
    private User client;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 8000)
    private String description;

    @Enumerated(EnumType.STRING)
    private Skill.Category category;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "job_skills", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "skill")
    private List<String> requiredSkills = new ArrayList<>();

    private BigDecimal budgetMin;
    private BigDecimal budgetMax;
    private String currency = "INR";

    @Enumerated(EnumType.STRING)
    private ProjectType projectType;

    @Enumerated(EnumType.STRING)
    private ExperienceLevel experienceLevel;

    private Integer durationWeeks;
    private boolean remote;

    @Enumerated(EnumType.STRING)
    private Status status = Status.OPEN;

    @Column(length = 10000)
    private String aiSpec;

    @CreationTimestamp
    private Instant createdAt;

    public enum ProjectType { FIXED, HOURLY }
    public enum ExperienceLevel { ENTRY, INTERMEDIATE, EXPERT }
    public enum Status { OPEN, IN_PROGRESS, CLOSED }

    public Job() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Job job = new Job();
        public Builder id(Long id) { job.id = id; return this; }
        public Builder client(User client) { job.client = client; return this; }
        public Builder title(String title) { job.title = title; return this; }
        public Builder description(String description) { job.description = description; return this; }
        public Builder category(Skill.Category category) { job.category = category; return this; }
        public Builder requiredSkills(List<String> requiredSkills) { job.requiredSkills = requiredSkills; return this; }
        public Builder budgetMin(BigDecimal budgetMin) { job.budgetMin = budgetMin; return this; }
        public Builder budgetMax(BigDecimal budgetMax) { job.budgetMax = budgetMax; return this; }
        public Builder currency(String currency) { job.currency = currency; return this; }
        public Builder projectType(ProjectType projectType) { job.projectType = projectType; return this; }
        public Builder experienceLevel(ExperienceLevel experienceLevel) { job.experienceLevel = experienceLevel; return this; }
        public Builder durationWeeks(Integer durationWeeks) { job.durationWeeks = durationWeeks; return this; }
        public Builder remote(boolean remote) { job.remote = remote; return this; }
        public Builder status(Status status) { job.status = status; return this; }
        public Builder aiSpec(String aiSpec) { job.aiSpec = aiSpec; return this; }
        public Job build() { return job; }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getClient() { return client; }
    public void setClient(User client) { this.client = client; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Skill.Category getCategory() { return category; }
    public void setCategory(Skill.Category category) { this.category = category; }
    public List<String> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills; }
    public BigDecimal getBudgetMin() { return budgetMin; }
    public void setBudgetMin(BigDecimal budgetMin) { this.budgetMin = budgetMin; }
    public BigDecimal getBudgetMax() { return budgetMax; }
    public void setBudgetMax(BigDecimal budgetMax) { this.budgetMax = budgetMax; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public ProjectType getProjectType() { return projectType; }
    public void setProjectType(ProjectType projectType) { this.projectType = projectType; }
    public ExperienceLevel getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(ExperienceLevel experienceLevel) { this.experienceLevel = experienceLevel; }
    public Integer getDurationWeeks() { return durationWeeks; }
    public void setDurationWeeks(Integer durationWeeks) { this.durationWeeks = durationWeeks; }
    public boolean isRemote() { return remote; }
    public void setRemote(boolean remote) { this.remote = remote; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getAiSpec() { return aiSpec; }
    public void setAiSpec(String aiSpec) { this.aiSpec = aiSpec; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
