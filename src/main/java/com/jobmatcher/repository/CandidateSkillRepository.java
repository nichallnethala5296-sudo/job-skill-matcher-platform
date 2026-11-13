package com.jobmatcher.repository;

import com.jobmatcher.entity.CandidateProfile;
import com.jobmatcher.entity.CandidateSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CandidateSkillRepository extends JpaRepository<CandidateSkill, Long> {
    List<CandidateSkill> findByCandidate(CandidateProfile candidate);
    void deleteByCandidate(CandidateProfile candidate);
}