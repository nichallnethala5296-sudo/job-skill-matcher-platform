package com.jobmatcher.repository;

import com.jobmatcher.entity.CourseResource;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseResourceRepository extends JpaRepository<CourseResource, Long> {
    List<CourseResource> findBySkillNameIgnoreCase(String skillName);
    List<CourseResource> findBySkillNameInIgnoreCase(List<String> skillNames);
}