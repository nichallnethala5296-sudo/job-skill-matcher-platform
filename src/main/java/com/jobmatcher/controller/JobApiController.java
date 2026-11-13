package com.jobmatcher.controller;

import com.jobmatcher.dto.MatchResultDto;
import com.jobmatcher.entity.*;
import com.jobmatcher.repository.*;
import com.jobmatcher.service.JobService;
import com.jobmatcher.service.SkillMatchingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class JobApiController {

    private final JobService jobService;
    private final UserRepository userRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final CandidateSkillRepository candidateSkillRepository;
    private final SkillMatchingService skillMatchingService;

    public JobApiController(JobService jobService,
                            UserRepository userRepository,
                            CandidateProfileRepository candidateProfileRepository,
                            CandidateSkillRepository candidateSkillRepository,
                            SkillMatchingService skillMatchingService) {
        this.jobService = jobService;
        this.userRepository = userRepository;
        this.candidateProfileRepository = candidateProfileRepository;
        this.candidateSkillRepository = candidateSkillRepository;
        this.skillMatchingService = skillMatchingService;
    }

    @GetMapping("/analytics/skills-frequency")
    public ResponseEntity<Map<String, Long>> getSkillFrequency() {
        List<CandidateSkill> candidateSkills = candidateSkillRepository.findAll();
        Map<String, Long> freq = new HashMap<>();
        for (CandidateSkill cs : candidateSkills) {
            String name = cs.getSkill().getNormalizedName();
            freq.put(name, freq.getOrDefault(name, 0L) + 1);
        }
        return ResponseEntity.ok(freq);
    }
}