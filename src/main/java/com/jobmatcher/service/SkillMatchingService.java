package com.jobmatcher.service;

import com.jobmatcher.dto.MatchResultDto;
import com.jobmatcher.entity.*;
import com.jobmatcher.repository.CourseResourceRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SkillMatchingService {

    private final CourseResourceRepository courseResourceRepository;

    public SkillMatchingService(CourseResourceRepository courseResourceRepository) {
        this.courseResourceRepository = courseResourceRepository;
    }

    public MatchResultDto calculateMatch(Job job, List<CandidateSkill> candidateSkills) {
        Set<String> candidateSkillSet = candidateSkills.stream()
                .map(cs -> cs.getSkill().getNormalizedName().toUpperCase().trim())
                .collect(Collectors.toSet());

        List<String> requiredList = splitSkills(job.getRequiredSkills());
        List<String> preferredList = splitSkills(job.getPreferredSkills());

        List<String> matchedReq = new ArrayList<>();
        List<String> missingReq = new ArrayList<>();
        for (String req : requiredList) {
            if (candidateSkillSet.contains(req)) matchedReq.add(req);
            else missingReq.add(req);
        }

        List<String> matchedPref = new ArrayList<>();
        List<String> missingPref = new ArrayList<>();
        for (String pref : preferredList) {
            if (candidateSkillSet.contains(pref)) matchedPref.add(pref);
            else missingPref.add(pref);
        }

        double reqRatio = requiredList.isEmpty() ? 1.0 : (double) matchedReq.size() / requiredList.size();
        double reqCoverage = Math.round(reqRatio * 1000.0) / 10.0;

        double overall;
        double prefCoverage = 0.0;
        if (preferredList.isEmpty()) {
            overall = reqCoverage;
        } else {
            double prefRatio = (double) matchedPref.size() / preferredList.size();
            prefCoverage = Math.round(prefRatio * 1000.0) / 10.0;
            overall = Math.round(((reqCoverage * 0.75) + (prefCoverage * 0.25)) * 10.0) / 10.0;
        }

        List<CourseResource> courseList = missingReq.isEmpty() ? Collections.emptyList()
                : courseResourceRepository.findBySkillNameInIgnoreCase(missingReq);

        MatchResultDto result = new MatchResultDto();
        result.setJob(job);
        result.setOverallScore(Math.min(overall, 100.0));
        result.setRequiredCoveragePercentage(reqCoverage);
        result.setPreferredCoveragePercentage(prefCoverage);
        result.setMatchedRequiredSkills(matchedReq);
        result.setMissingRequiredSkills(missingReq);
        result.setMatchedPreferredSkills(matchedPref);
        result.setMissingPreferredSkills(missingPref);
        result.setRecommendedCourses(courseList);

        String explanation = String.format(
                "Deterministic Fit: Matched %d of %d required skills (75%% weight = %.1f%%). Matched %d of %d preferred skills (25%% weight = %.1f%%). Total Deterministic Score = %.1f%%.",
                matchedReq.size(), requiredList.size(), reqCoverage,
                matchedPref.size(), preferredList.size(), prefCoverage, result.getOverallScore()
        );
        result.setCalculationExplanation(explanation);
        return result;
    }

    private List<String> splitSkills(String skills) {
        if (skills == null || skills.trim().isEmpty()) return Collections.emptyList();
        return Arrays.stream(skills.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toUpperCase)
                .distinct()
                .collect(Collectors.toList());
    }
}