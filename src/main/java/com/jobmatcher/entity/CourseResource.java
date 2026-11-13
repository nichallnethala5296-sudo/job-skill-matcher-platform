package com.jobmatcher.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "course_resources", indexes = {
    @Index(name = "idx_resource_skill", columnList = "skill_name")
})
public class CourseResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "skill_name", nullable = false, length = 80)
    private String skillName;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 100)
    private String provider;

    @Column(nullable = false, length = 500)
    private String url;

    public CourseResource() {}
    public CourseResource(String skillName, String title, String provider, String url) {
        this.skillName = skillName.toUpperCase().trim();
        this.title = title;
        this.provider = provider;
        this.url = url;
    }

    public Long getId() { return id; }
    public String getSkillName() { return skillName; }
    public String getTitle() { return title; }
    public String getProvider() { return provider; }
    public String getUrl() { return url; }
}