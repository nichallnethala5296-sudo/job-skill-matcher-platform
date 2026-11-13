package com.jobmatcher.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "skills", indexes = {
    @Index(name = "idx_skill_name", columnList = "normalized_name", unique = true)
})
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "normalized_name", nullable = false, unique = true, length = 80)
    private String normalizedName;

    @Column(length = 50)
    private String category;

    public Skill() {}
    public Skill(String normalizedName, String category) {
        this.normalizedName = normalizedName.toUpperCase().trim();
        this.category = category;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNormalizedName() { return normalizedName; }
    public void setNormalizedName(String normalizedName) { this.normalizedName = normalizedName.toUpperCase().trim(); }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}