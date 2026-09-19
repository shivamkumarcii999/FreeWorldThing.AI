package com.freeworldthing.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "messages")
public class Message {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String conversationKey;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @Column(nullable = false, length = 8000)
    private String body;

    private boolean read;

    @CreationTimestamp
    private Instant createdAt;

    public Message() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Message m = new Message();
        public Builder id(Long id) { m.id = id; return this; }
        public Builder conversationKey(String k) { m.conversationKey = k; return this; }
        public Builder sender(User s) { m.sender = s; return this; }
        public Builder recipient(User r) { m.recipient = r; return this; }
        public Builder body(String b) { m.body = b; return this; }
        public Builder read(boolean r) { m.read = r; return this; }
        public Builder createdAt(Instant c) { m.createdAt = c; return this; }
        public Message build() { return m; }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getConversationKey() { return conversationKey; }
    public void setConversationKey(String conversationKey) { this.conversationKey = conversationKey; }
    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }
    public User getRecipient() { return recipient; }
    public void setRecipient(User recipient) { this.recipient = recipient; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
