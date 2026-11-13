
package com.jobmatcher.service;

import com.jobmatcher.dto.JobPostDto;
import com.jobmatcher.entity.Job;
import com.jobmatcher.entity.User;
import com.jobmatcher.exception.ResourceNotFoundException;
import com.jobmatcher.repository.JobRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final MongoSyncService mongoSyncService;

    public JobService(JobRepository jobRepository,
                      MongoSyncService mongoSyncService) {
        this.jobRepository = jobRepository;
        this.mongoSyncService = mongoSyncService;
    }

    public Page<Job> findActiveJobs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return jobRepository.findByActiveTrue(pageable);
    }

    public Page<Job> searchJobs(String keyword, String location, String exp, String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return jobRepository.searchJobs(
                (keyword != null && !keyword.isBlank()) ? keyword.trim() : null,
                (location != null && !location.isBlank()) ? location.trim() : null,
                (exp != null && !exp.isBlank()) ? exp.trim() : null,
                (type != null && !type.isBlank()) ? type.trim() : null,
                pageable
        );
    }

    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job " + id + " was not found."));
    }

    public List<Job> getJobsByRecruiter(User recruiter) {
        return jobRepository.findByRecruiter(recruiter);
    }

    @Transactional
    public Job createJob(JobPostDto dto, User recruiter) {
        Job job = new Job();
        job.setRecruiter(recruiter);
        job.setTitle(dto.getTitle());
        job.setCompany(dto.getCompany());
        job.setLocation(dto.getLocation());
        job.setDescription(dto.getDescription());
        job.setRequiredSkills(dto.getRequiredSkills().toUpperCase());
        job.setPreferredSkills(dto.getPreferredSkills() != null ? dto.getPreferredSkills().toUpperCase() : "");
        job.setExperienceLevel(dto.getExperienceLevel());
        job.setJobType(dto.getJobType());
        job.setActive(dto.isActive());
        job.setCreatedAt(LocalDateTime.now());

        Job savedJob = jobRepository.save(job);
        mongoSyncService.syncJob(savedJob);
        return savedJob;
    }

    @Transactional
    public Job updateJob(Long id, JobPostDto dto, User recruiter) {
        Job job = getJobById(id);

        if (!job.getRecruiter().getId().equals(recruiter.getId())
                && !recruiter.getRole().equals("ROLE_ADMIN")) {
            throw new SecurityException("Unauthorized to modify job.");
        }

        job.setTitle(dto.getTitle());
        job.setCompany(dto.getCompany());
        job.setLocation(dto.getLocation());
        job.setDescription(dto.getDescription());
        job.setRequiredSkills(dto.getRequiredSkills().toUpperCase());
        job.setPreferredSkills(dto.getPreferredSkills() != null ? dto.getPreferredSkills().toUpperCase() : "");
        job.setExperienceLevel(dto.getExperienceLevel());
        job.setJobType(dto.getJobType());
        job.setActive(dto.isActive());

        Job updatedJob = jobRepository.save(job);
        mongoSyncService.syncJob(updatedJob);
        return updatedJob;
    }

    @Transactional
    public void deleteJob(Long id, User recruiter) {
        Job job = getJobById(id);

        if (!job.getRecruiter().getId().equals(recruiter.getId())
                && !recruiter.getRole().equals("ROLE_ADMIN")) {
            throw new SecurityException("Unauthorized to delete job.");
        }

        jobRepository.delete(job);
        mongoSyncService.deleteJob(id);
    }
}
