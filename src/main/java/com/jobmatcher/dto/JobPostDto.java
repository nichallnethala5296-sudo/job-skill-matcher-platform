package com.jobmatcher.dto;

import jakarta.validation.constraints.NotBlank;

public class JobPostDto {
    private Long id;
    @NotBlank(message = "Job title is required")
    private String title;
    @NotBlank(message = "Company is required")
    private String company;
    @NotBlank(message = "Location is required")
    private String location;
    @NotBlank(message = "Job description is required")
    private String description;
    @NotBlank(message = "At least one required skill is required (comma-separated)")
    private String requiredSkills;
    private String preferredSkills;
    @NotBlank(message = "Experience level is required")
    private String experienceLevel;
    @NotBlank(message = "Job type is required")
    private String jobType;
    private boolean active = true;

    public JobPostDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
}