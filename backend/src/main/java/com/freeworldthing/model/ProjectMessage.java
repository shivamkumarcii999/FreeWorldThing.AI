package com.freeworldthing.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "project_messages")
public class ProjectMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long workspaceId;

    @Column(nullable = false)
    private Long senderId;

    private String senderName;
    private String senderRole; // CLIENT, FREELANCER, AI_ASSISTANT

    @Column(length = 4000, nullable = false)
    private String content;

    private String messageType = "TEXT"; // TEXT, CODE, FILE, AI_INSIGHT, MILESTONE_UPDATE
    private LocalDateTime createdAt;

    public ProjectMessage() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.messageType == null) {
            this.messageType = "TEXT";
        }
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private ProjectMessage m = new ProjectMessage();
        public Builder id(Long id) { m.id = id; return this; }
        public Builder workspaceId(Long workspaceId) { m.workspaceId = workspaceId; return this; }
        public Builder senderId(Long senderId) { m.senderId = senderId; return this; }
        public Builder senderName(String senderName) { m.senderName = senderName; return this; }
        public Builder senderRole(String senderRole) { m.senderRole = senderRole; return this; }
        public Builder content(String content) { m.content = content; return this; }
        public Builder messageType(String messageType) { m.messageType = messageType; return this; }
        public Builder createdAt(LocalDateTime createdAt) { m.createdAt = createdAt; return this; }
        public ProjectMessage build() { return m; }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(Long workspaceId) { this.workspaceId = workspaceId; }
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public String getSenderRole() { return senderRole; }
    public void setSenderRole(String senderRole) { this.senderRole = senderRole; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
