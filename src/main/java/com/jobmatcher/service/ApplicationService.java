package com.jobmatcher.service;

import com.jobmatcher.entity.*;
import com.jobmatcher.exception.ResourceNotFoundException;
import com.jobmatcher.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final SavedJobRepository savedJobRepository;
    private final CandidateSkillRepository candidateSkillRepository;
    private final SkillMatchingService skillMatchingService;

    public ApplicationService(JobApplicationRepository applicationRepository,
                              SavedJobRepository savedJobRepository,
                              CandidateSkillRepository candidateSkillRepository,
                              SkillMatchingService skillMatchingService) {
        this.applicationRepository = applicationRepository;
        this.savedJobRepository = savedJobRepository;
        this.candidateSkillRepository = candidateSkillRepository;
        this.skillMatchingService = skillMatchingService;
    }

    @Transactional
    public JobApplication applyToJob(CandidateProfile candidate, Job job) {
        if (applicationRepository.existsByCandidateAndJob(candidate, job)) {
            throw new IllegalStateException("You have already submitted an application for this position.");
        }
        List<CandidateSkill> skills = candidateSkillRepository.findByCandidate(candidate);
        double score = skillMatchingService.calculateMatch(job, skills).getOverallScore();
        return applicationRepository.save(new JobApplication(candidate, job, score));
    }

    @Transactional
    public boolean toggleSaveJob(CandidateProfile candidate, Job job) {
        var existing = savedJobRepository.findByCandidateAndJob(candidate, job);
        if (existing.isPresent()) {
            savedJobRepository.delete(existing.get());
            return false;
        } else {
            savedJobRepository.save(new SavedJob(candidate, job));
            return true;
        }
    }

    public List<JobApplication> getApplicationsForCandidate(CandidateProfile candidate) {
        return applicationRepository.findByCandidate(candidate);
    }

    public List<SavedJob> getSavedJobsForCandidate(CandidateProfile candidate) {
        return savedJobRepository.findByCandidate(candidate);
    }

    public List<JobApplication> getApplicationsForRecruiter(Long recruiterId) {
        return applicationRepository.findByJob_Recruiter_Id(recruiterId);
    }

    @Transactional
    public JobApplication updateApplicationStatus(Long applicationId, String status, Long recruiterId, boolean isAdmin) {
        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application was not found."));

        if (!isAdmin && !application.getJob().getRecruiter().getId().equals(recruiterId)) {
            throw new SecurityException("Unauthorized application status update.");
        }
        application.setStatus(status.toUpperCase());
        return applicationRepository.save(application);
    }
}