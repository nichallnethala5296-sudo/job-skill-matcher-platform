package com.jobmatcher.repository;

import com.jobmatcher.entity.Job;
import com.jobmatcher.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    Page<Job> findByActiveTrue(Pageable pageable);
    List<Job> findByRecruiter(User recruiter);

    @Query("SELECT j FROM Job j WHERE j.active = true AND " +
           "(:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(j.company) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:exp IS NULL OR j.experienceLevel = :exp) AND " +
           "(:type IS NULL OR j.jobType = :type)")
    Page<Job> searchJobs(@Param("keyword") String keyword,
                         @Param("location") String location,
                         @Param("exp") String exp,
                         @Param("type") String type,
                         Pageable pageable);
}