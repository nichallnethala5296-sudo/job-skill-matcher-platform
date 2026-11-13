package com.jobmatcher.repository;

import com.jobmatcher.entity.CandidateProfile;
import com.jobmatcher.entity.Job;
import com.jobmatcher.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByCandidate(CandidateProfile candidate);
    List<JobApplication> findByJob(Job job);
    List<JobApplication> findByJob_Recruiter_Id(Long recruiterId);
    Optional<JobApplication> findByCandidateAndJob(CandidateProfile candidate, Job job);
    boolean existsByCandidateAndJob(CandidateProfile candidate, Job job);
}