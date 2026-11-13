package com.jobmatcher.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "saved_jobs", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"candidate_id", "job_id"})
})
public class SavedJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private CandidateProfile candidate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public SavedJob() {}
    public SavedJob(CandidateProfile candidate, Job job) {
        this.candidate = candidate;
        this.job = job;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public CandidateProfile getCandidate() { return candidate; }
    public Job getJob() { return job; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}