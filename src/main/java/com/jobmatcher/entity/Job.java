package com.jobmatcher.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs", indexes = {
    @Index(name = "idx_job_active", columnList = "active"),
    @Index(name = "idx_job_experience", columnList = "experience_level"),
    @Index(name = "idx_job_location", columnList = "location")
})
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id", nullable = false)
    private User recruiter;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String title;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String company;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String location;

    @Lob
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String description;

    @Column(name = "required_skills", nullable = false, length = 1000)
    private String requiredSkills;

    @Column(name = "preferred_skills", length = 1000)
    private String preferredSkills;

    @Column(name = "experience_level", nullable = false, length = 30)
    private String experienceLevel;

    @Column(name = "job_type", nullable = false, length = 30)
    private String jobType;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Job() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getRecruiter() { return recruiter; }
    public void setRecruiter(User recruiter) { this.recruiter = recruiter; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(String requiredSkills) { this.requiredSkills = requiredSkills; }
    public String getPreferredSkills() { return preferredSkills; }
    public void setPreferredSkills(String preferredSkills) { this.preferredSkills = preferredSkills; }
    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }
    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}