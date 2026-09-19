package com.freeworldthing.model;

import jakarta.persistence.*;

@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    private String title;

    @Column(length = 4000)
    private String bio;

    private Double hourlyRate;
    private String currency = "INR";
    private Double rating;
    private Integer reviewCount;
    private Integer completedProjects;
    private String location;
    private String githubUrl;
    private String skills;
    private Double proofOfWorkScore;
    private Boolean verifiedBadge;
    private Boolean availableNow;

    public Profile() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Profile p = new Profile();
        public Builder id(Long id) { p.id = id; return this; }
        public Builder userId(Long userId) { p.userId = userId; return this; }
        public Builder title(String title) { p.title = title; return this; }
        public Builder bio(String bio) { p.bio = bio; return this; }
        public Builder hourlyRate(Double hourlyRate) { p.hourlyRate = hourlyRate; return this; }
        public Builder currency(String currency) { p.currency = currency; return this; }
        public Builder rating(Double rating) { p.rating = rating; return this; }
        public Builder reviewCount(Integer reviewCount) { p.reviewCount = reviewCount; return this; }
        public Builder completedProjects(Integer completedProjects) { p.completedProjects = completedProjects; return this; }
        public Builder location(String location) { p.location = location; return this; }
        public Builder githubUrl(String githubUrl) { p.githubUrl = githubUrl; return this; }
        public Builder skills(String skills) { p.skills = skills; return this; }
        public Builder proofOfWorkScore(Double score) { p.proofOfWorkScore = score; return this; }
        public Builder verifiedBadge(Boolean badge) { p.verifiedBadge = badge; return this; }
        public Builder availableNow(Boolean avail) { p.availableNow = avail; return this; }
        public Profile build() { return p; }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public Double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(Double hourlyRate) { this.hourlyRate = hourlyRate; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
    public Integer getCompletedProjects() { return completedProjects; }
    public void setCompletedProjects(Integer completedProjects) { this.completedProjects = completedProjects; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getGithubUrl() { return githubUrl; }
    public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    public Double getProofOfWorkScore() { return proofOfWorkScore; }
    public void setProofOfWorkScore(Double proofOfWorkScore) { this.proofOfWorkScore = proofOfWorkScore; }
    public Boolean getVerifiedBadge() { return verifiedBadge; }
    public void setVerifiedBadge(Boolean verifiedBadge) { this.verifiedBadge = verifiedBadge; }
    public Boolean getAvailableNow() { return availableNow; }
    public void setAvailableNow(Boolean availableNow) { this.availableNow = availableNow; }
}
