package com.freeworldthing.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_skills", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "skill_id"}))
public class UserSkill {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "skill_id")
    private Skill skill;

    private int level;
    private boolean verified;

    @Column(length = 500)
    private String evidence;

    public UserSkill() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UserSkill us = new UserSkill();
        public Builder id(Long id) { us.id = id; return this; }
        public Builder user(User user) { us.user = user; return this; }
        public Builder skill(Skill skill) { us.skill = skill; return this; }
        public Builder level(int level) { us.level = level; return this; }
        public Builder verified(boolean verified) { us.verified = verified; return this; }
        public Builder evidence(String evidence) { us.evidence = evidence; return this; }
        public UserSkill build() { return us; }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Skill getSkill() { return skill; }
    public void setSkill(Skill skill) { this.skill = skill; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
    public String getEvidence() { return evidence; }
    public void setEvidence(String evidence) { this.evidence = evidence; }
}
