package com.jobmatcher.controller;

import com.jobmatcher.dto.JobPostDto;
import com.jobmatcher.entity.*;
import com.jobmatcher.repository.*;
import com.jobmatcher.service.ApplicationService;
import com.jobmatcher.service.JobService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/recruiter")
public class RecruiterController {

    private final UserRepository userRepository;
    private final JobService jobService;
    private final ApplicationService applicationService;
    private final CandidateProfileRepository candidateProfileRepository;

    public RecruiterController(UserRepository userRepository,
                               JobService jobService,
                               ApplicationService applicationService,
                               CandidateProfileRepository candidateProfileRepository) {
        this.userRepository = userRepository;
        this.jobService = jobService;
        this.applicationService = applicationService;
        this.candidateProfileRepository = candidateProfileRepository;
    }

    private User getRecruiter(UserDetails ud) {
        return userRepository.findByEmail(ud.getUsername()).orElseThrow();
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails ud, Model model) {
        User r = getRecruiter(ud);
        var jobs = jobService.getJobsByRecruiter(r);
        var apps = applicationService.getApplicationsForRecruiter(r.getId());
        model.addAttribute("totalJobs", jobs.size());
        model.addAttribute("totalApplicants", apps.size());
        model.addAttribute("applications", apps);
        return "recruiter/dashboard";
    }

    @GetMapping("/jobs/new")
    public String newJob(Model model) {
        model.addAttribute("jobDto", new JobPostDto());
        return "recruiter/job-form";
    }

    @PostMapping("/jobs/save")
    public String saveJob(@AuthenticationPrincipal UserDetails ud,
                          @Valid @ModelAttribute("jobDto") JobPostDto dto,
                          BindingResult res,
                          RedirectAttributes ra) {
        if (res.hasErrors()) return "recruiter/job-form";
        User r = getRecruiter(ud);
        if (dto.getId() == null) jobService.createJob(dto, r);
        else jobService.updateJob(dto.getId(), dto, r);
        ra.addFlashAttribute("successMessage", "Job position saved.");
        return "redirect:/recruiter/manage-jobs";
    }

    @GetMapping("/manage-jobs")
    public String manageJobs(@AuthenticationPrincipal UserDetails ud, Model model) {
        model.addAttribute("jobs", jobService.getJobsByRecruiter(getRecruiter(ud)));
        return "recruiter/manage-jobs";
    }

    @PostMapping("/applications/status")
    public String updateStatus(@AuthenticationPrincipal UserDetails ud,
                               @RequestParam Long applicationId,
                               @RequestParam String status,
                               RedirectAttributes ra) {
        applicationService.updateApplicationStatus(applicationId, status, getRecruiter(ud).getId(), false);
        ra.addFlashAttribute("successMessage", "Application status updated to " + status);
        return "redirect:/recruiter/dashboard";
    }

    @GetMapping("/candidates")
    public String candidateSearch(Model model) {
        model.addAttribute("candidates", candidateProfileRepository.findAll());
        return "recruiter/candidate-search";
    }
}