package com.jobmatcher.dto;

import com.jobmatcher.entity.CourseResource;
import com.jobmatcher.entity.Job;
import java.util.List;

public class MatchResultDto {
    private Job job;
    private double overallScore;
    private double requiredCoveragePercentage;
    private double preferredCoveragePercentage;
    private List<String> matchedRequiredSkills;
    private List<String> missingRequiredSkills;
    private List<String> matchedPreferredSkills;
    private List<String> missingPreferredSkills;
    private List<CourseResource> recommendedCourses;
    private String calculationExplanation;

    public MatchResultDto() {}

    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }
    public double getOverallScore() { return overallScore; }
    public void setOverallScore(double overallScore) { this.overallScore = overallScore; }
    public double getRequiredCoveragePercentage() { return requiredCoveragePercentage; }
    public void setRequiredCoveragePercentage(double requiredCoveragePercentage) { this.requiredCoveragePercentage = requiredCoveragePercentage; }
    public double getPreferredCoveragePercentage() { return preferredCoveragePercentage; }
    public void setPreferredCoveragePercentage(double preferredCoveragePercentage) { this.preferredCoveragePercentage = preferredCoveragePercentage; }
    public List<String> getMatchedRequiredSkills() { return matchedRequiredSkills; }
    public void setMatchedRequiredSkills(List<String> matchedRequiredSkills) { this.matchedRequiredSkills = matchedRequiredSkills; }
    public List<String> getMissingRequiredSkills() { return missingRequiredSkills; }
    public void setMissingRequiredSkills(List<String> missingRequiredSkills) { this.missingRequiredSkills = missingRequiredSkills; }
    public List<String> getMatchedPreferredSkills() { return matchedPreferredSkills; }
    public void setMatchedPreferredSkills(List<String> matchedPreferredSkills) { this.matchedPreferredSkills = matchedPreferredSkills; }
    public List<String> getMissingPreferredSkills() { return missingPreferredSkills; }
    public void setMissingPreferredSkills(List<String> missingPreferredSkills) { this.missingPreferredSkills = missingPreferredSkills; }
    public List<CourseResource> getRecommendedCourses() { return recommendedCourses; }
    public void setRecommendedCourses(List<CourseResource> recommendedCourses) { this.recommendedCourses = recommendedCourses; }
    public String getCalculationExplanation() { return calculationExplanation; }
    public void setCalculationExplanation(String calculationExplanation) { this.calculationExplanation = calculationExplanation; }
}