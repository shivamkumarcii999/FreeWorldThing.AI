package com.freeworldthing.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "project_workspaces")
public class ProjectWorkspace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long contractId;

    private String healthStatus;

    @Column(length = 3000)
    private String aiExecutiveSummary;

    @Column(length = 2000)
    private String blockersJson;

    @Column(length = 2000)
    private String nextStepsJson;

    private LocalDateTime lastAiScanAt;

    public ProjectWorkspace() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private ProjectWorkspace pw = new ProjectWorkspace();
        public Builder id(Long id) { pw.id = id; return this; }
        public Builder contractId(Long contractId) { pw.contractId = contractId; return this; }
        public Builder healthStatus(String healthStatus) { pw.healthStatus = healthStatus; return this; }
        public Builder aiExecutiveSummary(String summary) { pw.aiExecutiveSummary = summary; return this; }
        public Builder blockersJson(String blockers) { pw.blockersJson = blockers; return this; }
        public Builder nextStepsJson(String steps) { pw.nextStepsJson = steps; return this; }
        public Builder lastAiScanAt(LocalDateTime lastAiScanAt) { pw.lastAiScanAt = lastAiScanAt; return this; }
        public ProjectWorkspace build() { return pw; }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getContractId() { return contractId; }
    public void setContractId(Long contractId) { this.contractId = contractId; }
    public String getHealthStatus() { return healthStatus; }
    public void setHealthStatus(String healthStatus) { this.healthStatus = healthStatus; }
    public String getAiExecutiveSummary() { return aiExecutiveSummary; }
    public void setAiExecutiveSummary(String aiExecutiveSummary) { this.aiExecutiveSummary = aiExecutiveSummary; }
    public String getBlockersJson() { return blockersJson; }
    public void setBlockersJson(String blockersJson) { this.blockersJson = blockersJson; }
    public String getNextStepsJson() { return nextStepsJson; }
    public void setNextStepsJson(String nextStepsJson) { this.nextStepsJson = nextStepsJson; }
    public LocalDateTime getLastAiScanAt() { return lastAiScanAt; }
    public void setLastAiScanAt(LocalDateTime lastAiScanAt) { this.lastAiScanAt = lastAiScanAt; }
}
