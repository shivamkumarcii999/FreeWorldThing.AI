package com.freeworldthing.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_items")
public class TaskItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long workspaceId;

    private Long milestoneId;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.TODO;

    private String priority = "MEDIUM";
    private String assignedToName;
    private LocalDateTime dueDate;

    public enum TaskStatus {
        TODO,
        IN_PROGRESS,
        REVIEW,
        DONE
    }

    public TaskItem() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private TaskItem t = new TaskItem();
        public Builder id(Long id) { t.id = id; return this; }
        public Builder workspaceId(Long workspaceId) { t.workspaceId = workspaceId; return this; }
        public Builder milestoneId(Long milestoneId) { t.milestoneId = milestoneId; return this; }
        public Builder title(String title) { t.title = title; return this; }
        public Builder description(String description) { t.description = description; return this; }
        public Builder status(TaskStatus status) { t.status = status; return this; }
        public Builder priority(String priority) { t.priority = priority; return this; }
        public Builder assignedToName(String name) { t.assignedToName = name; return this; }
        public Builder dueDate(LocalDateTime dueDate) { t.dueDate = dueDate; return this; }
        public TaskItem build() { return t; }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(Long workspaceId) { this.workspaceId = workspaceId; }
    public Long getMilestoneId() { return milestoneId; }
    public void setMilestoneId(Long milestoneId) { this.milestoneId = milestoneId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getAssignedToName() { return assignedToName; }
    public void setAssignedToName(String assignedToName) { this.assignedToName = assignedToName; }
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
}
