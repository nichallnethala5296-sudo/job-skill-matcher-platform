package com.jobmatcher.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "candidate_skills", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"candidate_id", "skill_id"})
})
public class CandidateSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private CandidateProfile candidate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(name = "proficiency_source")
    private String proficiencySource;

    public CandidateSkill() {}

    public CandidateSkill(CandidateProfile candidate, Skill skill, String proficiencySource) {
        this.candidate = candidate;
        this.skill = skill;
        this.proficiencySource = proficiencySource;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public CandidateProfile getCandidate() { return candidate; }
    public void setCandidate(CandidateProfile candidate) { this.candidate = candidate; }
    public Skill getSkill() { return skill; }
    public void setSkill(Skill skill) { this.skill = skill; }
    public String getProficiencySource() { return proficiencySource; }
    public void setProficiencySource(String proficiencySource) { this.proficiencySource = proficiencySource; }
}