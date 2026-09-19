package com.freeworldthing.model;

import jakarta.persistence.*;

@Entity
@Table(name = "skills")
public class Skill {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    public enum Category { DEVELOPMENT, AI_ML, FULLSTACK, DESIGN, CREATIVE, BUSINESS, MARKETING, DATA }

    public Skill() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Skill s = new Skill();
        public Builder id(Long id) { s.id = id; return this; }
        public Builder name(String name) { s.name = name; return this; }
        public Builder category(Category category) { s.category = category; return this; }
        public Skill build() { return s; }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}
