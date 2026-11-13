package com.jobmatcher.repository;

import com.jobmatcher.entity.CandidateProfile;
import com.jobmatcher.entity.Job;
import com.jobmatcher.entity.SavedJob;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {
    List<SavedJob> findByCandidate(CandidateProfile candidate);
    Optional<SavedJob> findByCandidateAndJob(CandidateProfile candidate, Job job);
    boolean existsByCandidateAndJob(CandidateProfile candidate, Job job);
    void deleteByCandidateAndJob(CandidateProfile candidate, Job job);
}