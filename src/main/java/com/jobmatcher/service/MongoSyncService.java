
package com.jobmatcher.service;

import com.jobmatcher.entity.CandidateProfile;
import com.jobmatcher.entity.Job;
import com.jobmatcher.entity.User;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class MongoSyncService {

    private final MongoTemplate mongoTemplate;

    public MongoSyncService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    // Save or update recruiter job
    public void syncJob(Job job) {

        Query query = new Query(
                Criteria.where("jobId").is(job.getId())
        );

        Update update = new Update()
                .set("jobId", job.getId())
                .set("title", job.getTitle())
                .set("company", job.getCompany())
                .set("location", job.getLocation())
                .set("description", job.getDescription())
                .set("requiredSkills", job.getRequiredSkills())
                .set("preferredSkills", job.getPreferredSkills())
                .set("experienceLevel", job.getExperienceLevel())
                .set("jobType", job.getJobType())
                .set("active", job.isActive())
                .set("createdAt", job.getCreatedAt());

        if (job.getRecruiter() != null) {
            update.set("recruiterId", job.getRecruiter().getId())
                  .set("recruiterName", job.getRecruiter().getName())
                  .set("recruiterEmail", job.getRecruiter().getEmail());
        }

        mongoTemplate.upsert(query, update, "recruiter_jobs");
    }

    // Save or update candidate profile
    public void syncCandidate(CandidateProfile profile, User user) {

        Query query = new Query(
                Criteria.where("candidateId").is(profile.getId())
        );

        Update update = new Update()
                .set("candidateId", profile.getId())
                .set("userId", user.getId())
                .set("name", user.getName())
                .set("email", user.getEmail())
                .set("role", user.getRole())
                .set("headline", profile.getHeadline())
                .set("location", profile.getLocation())
                .set("experienceLevel", profile.getExperienceLevel())
                .set("resumePath", profile.getResumePath())
                .set("extractedResumeText", profile.getExtractedResumeText());

        mongoTemplate.upsert(query, update, "candidate_profiles");
    }

    // Delete recruiter job
    public void deleteJob(Long id) {

        Query query = new Query(
                Criteria.where("jobId").is(id)
        );

        mongoTemplate.remove(query, "recruiter_jobs");
    }
}
