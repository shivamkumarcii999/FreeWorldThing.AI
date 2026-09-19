package com.freeworldthing.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String fullName;

    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // CLIENT, FREELANCER, ADMIN

    private String headline;
    @Column(length = 4000)
    private String bio;
    private String location;
    private Double hourlyRate;
    private Double walletBalance = 0.0;

    @Enumerated(EnumType.STRING)
    private Availability availability; // AVAILABLE, WITHIN_WEEK, BUSY

    private Double ratingAvg;
    private Integer ratingCount;
    private Integer completedProjects;

    private boolean identityVerified;
    private boolean paymentVerified;
    private boolean businessVerified;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_specialties", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "specialty")
    private List<String> specialties = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<UserSkill> skills = new ArrayList<>();

    @CreationTimestamp
    private Instant createdAt;

    public enum Role { CLIENT, FREELANCER, ADMIN }
    public enum Availability { AVAILABLE, WITHIN_WEEK, BUSY }

    public User() {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private User user = new User();

        public Builder id(Long id) { user.id = id; return this; }
        public Builder email(String email) { user.email = email; return this; }
        public Builder passwordHash(String passwordHash) { user.passwordHash = passwordHash; return this; }
        public Builder fullName(String fullName) { user.fullName = fullName; return this; }
        public Builder avatarUrl(String avatarUrl) { user.avatarUrl = avatarUrl; return this; }
        public Builder role(Role role) { user.role = role; return this; }
        public Builder headline(String headline) { user.headline = headline; return this; }
        public Builder bio(String bio) { user.bio = bio; return this; }
        public Builder location(String location) { user.location = location; return this; }
        public Builder hourlyRate(Double hourlyRate) { user.hourlyRate = hourlyRate; return this; }
        public Builder walletBalance(Double walletBalance) { user.walletBalance = walletBalance; return this; }
        public Builder availability(Availability availability) { user.availability = availability; return this; }
        public Builder ratingAvg(Double ratingAvg) { user.ratingAvg = ratingAvg; return this; }
        public Builder ratingCount(Integer ratingCount) { user.ratingCount = ratingCount; return this; }
        public Builder completedProjects(Integer completedProjects) { user.completedProjects = completedProjects; return this; }
        public Builder identityVerified(boolean v) { user.identityVerified = v; return this; }
        public Builder paymentVerified(boolean v) { user.paymentVerified = v; return this; }
        public Builder businessVerified(boolean v) { user.businessVerified = v; return this; }
        public Builder specialties(List<String> specialties) { user.specialties = specialties; return this; }
        public User build() { return user; }
    }

    public void addSkill(Skill skill, int level, boolean verified, String evidence) {
        UserSkill us = new UserSkill();
        us.setUser(this);
        us.setSkill(skill);
        us.setLevel(level);
        us.setVerified(verified);
        us.setEvidence(evidence);
        this.skills.add(us);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    @JsonIgnore
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public String getHeadline() { return headline; }
    public void setHeadline(String headline) { this.headline = headline; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(Double hourlyRate) { this.hourlyRate = hourlyRate; }
    public Double getWalletBalance() { return walletBalance; }
    public void setWalletBalance(Double walletBalance) { this.walletBalance = walletBalance; }
    public Availability getAvailability() { return availability; }
    public void setAvailability(Availability availability) { this.availability = availability; }
    public Double getRatingAvg() { return ratingAvg; }
    public void setRatingAvg(Double ratingAvg) { this.ratingAvg = ratingAvg; }
    public Integer getRatingCount() { return ratingCount; }
    public void setRatingCount(Integer ratingCount) { this.ratingCount = ratingCount; }
    public Integer getCompletedProjects() { return completedProjects; }
    public void setCompletedProjects(Integer completedProjects) { this.completedProjects = completedProjects; }
    public boolean isIdentityVerified() { return identityVerified; }
    public void setIdentityVerified(boolean identityVerified) { this.identityVerified = identityVerified; }
    public boolean isPaymentVerified() { return paymentVerified; }
    public void setPaymentVerified(boolean paymentVerified) { this.paymentVerified = paymentVerified; }
    public boolean isBusinessVerified() { return businessVerified; }
    public void setBusinessVerified(boolean businessVerified) { this.businessVerified = businessVerified; }
    public List<String> getSpecialties() { return specialties; }
    public void setSpecialties(List<String> specialties) { this.specialties = specialties; }
    public List<UserSkill> getSkills() { return skills; }
    public void setSkills(List<UserSkill> skills) { this.skills = skills; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
