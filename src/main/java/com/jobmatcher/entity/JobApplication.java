package com.jobmatcher.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_applications", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"candidate_id", "job_id"})
})
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "candidate_id", nullable = false)
    private CandidateProfile candidate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Column(name = "match_score_at_application", nullable = false)
    private double matchScoreAtApplication;

    @Column(nullable = false, length = 30)
    private String status = "APPLIED";

    @Column(name = "applied_at", nullable = false)
    private LocalDateTime appliedAt = LocalDateTime.now();

    public JobApplication() {}

    public JobApplication(CandidateProfile candidate, Job job, double matchScoreAtApplication) {
        this.candidate = candidate;
        this.job = job;
        this.matchScoreAtApplication = matchScoreAtApplication;
        this.status = "APPLIED";
        this.appliedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public CandidateProfile getCandidate() { return candidate; }
    public void setCandidate(CandidateProfile candidate) { this.candidate = candidate; }
    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }
    public double getMatchScoreAtApplication() { return matchScoreAtApplication; }
    public void setMatchScoreAtApplication(double score) { this.matchScoreAtApplication = score; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }
}