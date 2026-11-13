package com.jobmatcher.controller;

import com.jobmatcher.entity.Job;
import com.jobmatcher.service.JobService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class JobViewController {

    private final JobService jobService;

    public JobViewController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/jobs")
    public String browseJobs(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(required = false) String keyword,
                             @RequestParam(required = false) String location,
                             @RequestParam(required = false) String type,
                             Model model) {
        Page<Job> jobs = jobService.searchJobs(keyword, location, null, type, page, 10);
        model.addAttribute("jobsPage", jobs);
        return "jobs/listings";
    }

    @GetMapping("/jobs/view/{id}")
    public String jobDetails(@PathVariable Long id, Model model) {
        model.addAttribute("job", jobService.getJobById(id));
        return "jobs/details";
    }
}