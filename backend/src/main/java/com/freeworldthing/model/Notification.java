package com.freeworldthing.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 60)
    private String type;

    @Column(nullable = false, length = 300)
    private String text;

    private String link;

    private boolean read;

    @CreationTimestamp
    private Instant createdAt;

    public Notification() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Notification n = new Notification();
        public Builder id(Long id) { n.id = id; return this; }
        public Builder user(User user) { n.user = user; return this; }
        public Builder type(String type) { n.type = type; return this; }
        public Builder text(String text) { n.text = text; return this; }
        public Builder link(String link) { n.link = link; return this; }
        public Builder read(boolean read) { n.read = read; return this; }
        public Builder createdAt(Instant createdAt) { n.createdAt = createdAt; return this; }
        public Notification build() { return n; }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
