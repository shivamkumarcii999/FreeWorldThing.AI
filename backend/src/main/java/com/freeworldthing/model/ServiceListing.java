package com.freeworldthing.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "service_listings")
public class ServiceListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long freelancerId;

    private String freelancerName;
    private String freelancerAvatar;

    @Column(nullable = false)
    private String title;

    @Column(length = 4000)
    private String description;

    private String category;
    private Double startingPrice;
    private String currency = "INR";
    private Double rating;
    private Integer reviewCount;
    private Integer deliveryDays;

    @Column(length = 4000)
    private String packagesJson;

    @Column(length = 2000)
    private String addOnsJson;

    private LocalDateTime createdAt;

    public ServiceListing() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.currency == null) {
            this.currency = "INR";
        }
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private ServiceListing sl = new ServiceListing();
        public Builder id(Long id) { sl.id = id; return this; }
        public Builder freelancerId(Long freelancerId) { sl.freelancerId = freelancerId; return this; }
        public Builder freelancerName(String freelancerName) { sl.freelancerName = freelancerName; return this; }
        public Builder freelancerAvatar(String freelancerAvatar) { sl.freelancerAvatar = freelancerAvatar; return this; }
        public Builder title(String title) { sl.title = title; return this; }
        public Builder description(String description) { sl.description = description; return this; }
        public Builder category(String category) { sl.category = category; return this; }
        public Builder startingPrice(Double startingPrice) { sl.startingPrice = startingPrice; return this; }
        public Builder currency(String currency) { sl.currency = currency; return this; }
        public Builder rating(Double rating) { sl.rating = rating; return this; }
        public Builder reviewCount(Integer reviewCount) { sl.reviewCount = reviewCount; return this; }
        public Builder deliveryDays(Integer deliveryDays) { sl.deliveryDays = deliveryDays; return this; }
        public Builder packagesJson(String packagesJson) { sl.packagesJson = packagesJson; return this; }
        public Builder addOnsJson(String addOnsJson) { sl.addOnsJson = addOnsJson; return this; }
        public ServiceListing build() { return sl; }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getFreelancerId() { return freelancerId; }
    public void setFreelancerId(Long freelancerId) { this.freelancerId = freelancerId; }
    public String getFreelancerName() { return freelancerName; }
    public void setFreelancerName(String freelancerName) { this.freelancerName = freelancerName; }
    public String getFreelancerAvatar() { return freelancerAvatar; }
    public void setFreelancerAvatar(String freelancerAvatar) { this.freelancerAvatar = freelancerAvatar; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Double getStartingPrice() { return startingPrice; }
    public void setStartingPrice(Double startingPrice) { this.startingPrice = startingPrice; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
    public Integer getDeliveryDays() { return deliveryDays; }
    public void setDeliveryDays(Integer deliveryDays) { this.deliveryDays = deliveryDays; }
    public String getPackagesJson() { return packagesJson; }
    public void setPackagesJson(String packagesJson) { this.packagesJson = packagesJson; }
    public String getAddOnsJson() { return addOnsJson; }
    public void setAddOnsJson(String addOnsJson) { this.addOnsJson = addOnsJson; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
