package com.jobmatcher.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "candidate_profiles")
public class CandidateProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String headline;
    private String location;
    private String experienceLevel;

    @Column(name = "resume_path")
    private String resumePath;

    @Lob
    @Column(name = "extracted_resume_text", columnDefinition = "LONGTEXT")
    private String extractedResumeText;

    public CandidateProfile() {}

    public CandidateProfile(User user) {
        this.user = user;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getHeadline() { return headline; }
    public void setHeadline(String headline) { this.headline = headline; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }
    public String getResumePath() { return resumePath; }
    public void setResumePath(String resumePath) { this.resumePath = resumePath; }
    public String getExtractedResumeText() { return extractedResumeText; }
    public void setExtractedResumeText(String extractedResumeText) { this.extractedResumeText = extractedResumeText; }
}