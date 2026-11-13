package com.jobmatcher.service;

import com.jobmatcher.dto.MatchResultDto;
import com.jobmatcher.entity.*;
import com.jobmatcher.repository.CourseResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SkillMatchingServiceTest {

    @Mock
    private CourseResourceRepository courseResourceRepository;

    private SkillMatchingService matchingService;

    @BeforeEach
    void setUp() {
        matchingService = new SkillMatchingService(courseResourceRepository);
    }

    @Test
    void testPartialMatchCalculation() {
        Job job = new Job();
        job.setRequiredSkills("JAVA, PYTHON");
        job.setPreferredSkills("AWS");

        CandidateProfile profile = new CandidateProfile();
        List<CandidateSkill> skills = List.of(
                new CandidateSkill(profile, new Skill("JAVA", "BACKEND"), "MANUAL")
        );

        MatchResultDto result = matchingService.calculateMatch(job, skills);

        // Required coverage: 1 of 2 = 50.0%. Weight 75% = 37.5%.
        assertEquals(37.5, result.getOverallScore(), 0.01);
        assertEquals(1, result.getMissingRequiredSkills().size());
        assertEquals("PYTHON", result.getMissingRequiredSkills().get(0));
    }
}