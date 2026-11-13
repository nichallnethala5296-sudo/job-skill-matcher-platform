
package com.jobmatcher.controller;

import com.jobmatcher.config.StorageConfig;
import com.jobmatcher.dto.MatchResultDto;
import com.jobmatcher.entity.*;
import com.jobmatcher.repository.*;
import com.jobmatcher.service.ApplicationService;
import com.jobmatcher.service.MongoSyncService;
import com.jobmatcher.service.ResumeParserService;
import com.jobmatcher.service.SkillMatchingService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;

@Controller
@RequestMapping("/candidate")
public class CandidateController {

    private final UserRepository userRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final CandidateSkillRepository candidateSkillRepository;
    private final ResumeParserService resumeParserService;
    private final JobRepository jobRepository;
    private final SkillMatchingService skillMatchingService;
    private final ApplicationService applicationService;
    private final StorageConfig storageConfig;
    private final MongoSyncService mongoSyncService;

    public CandidateController(UserRepository userRepository,
                               CandidateProfileRepository candidateProfileRepository,
                               CandidateSkillRepository candidateSkillRepository,
                               ResumeParserService resumeParserService,
                               JobRepository jobRepository,
                               SkillMatchingService skillMatchingService,
                               ApplicationService applicationService,
                               StorageConfig storageConfig,
                               MongoSyncService mongoSyncService) {
        this.userRepository = userRepository;
        this.candidateProfileRepository = candidateProfileRepository;
        this.candidateSkillRepository = candidateSkillRepository;
        this.resumeParserService = resumeParserService;
        this.jobRepository = jobRepository;
        this.skillMatchingService = skillMatchingService;
        this.applicationService = applicationService;
        this.storageConfig = storageConfig;
        this.mongoSyncService = mongoSyncService;
    }

    private CandidateProfile getProfile(UserDetails ud) {
        User u = userRepository.findByEmail(ud.getUsername()).orElseThrow();

        CandidateProfile profile = candidateProfileRepository.findByUser(u)
                .orElseGet(() -> candidateProfileRepository.save(new CandidateProfile(u)));

        mongoSyncService.syncCandidate(profile, u);

        return profile;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails ud, Model model) {
        CandidateProfile p = getProfile(ud);
        model.addAttribute("profile", p);
        model.addAttribute("skills", candidateSkillRepository.findByCandidate(p));
        model.addAttribute("applicationsCount", applicationService.getApplicationsForCandidate(p).size());
        model.addAttribute("savedCount", applicationService.getSavedJobsForCandidate(p).size());
        model.addAttribute("recentApplications", applicationService.getApplicationsForCandidate(p));
        return "candidate/dashboard";
    }

    @GetMapping("/resume-upload")
    public String resumeUploadPage(@AuthenticationPrincipal UserDetails ud, Model model) {
        CandidateProfile p = getProfile(ud);
        model.addAttribute("profile", p);
        model.addAttribute("skills", candidateSkillRepository.findByCandidate(p));
        return "candidate/resume-upload";
    }

    @PostMapping("/resume-upload")
    public String handleResumeUpload(@AuthenticationPrincipal UserDetails ud,
                                     @RequestParam("resumeFile") MultipartFile file,
                                     RedirectAttributes ra) {
        try {
            CandidateProfile p = getProfile(ud);
            Path path = resumeParserService.storeFileSafely(file, storageConfig.getUploadPath(), p.getUser().getId());
            String text = resumeParserService.extractTextFromFile(file);
            var skills = resumeParserService.parseAndSaveSkills(p, text, path);
            ra.addFlashAttribute("successMessage", "Processed! Extracted " + skills.size() + " skills.");
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/candidate/resume-upload";
    }

    @GetMapping("/match-analysis/{jobId}")
    public String matchAnalysis(@PathVariable Long jobId, @AuthenticationPrincipal UserDetails ud, Model model) {
        CandidateProfile p = getProfile(ud);
        Job job = jobRepository.findById(jobId).orElseThrow();
        MatchResultDto match = skillMatchingService.calculateMatch(job, candidateSkillRepository.findByCandidate(p));
        model.addAttribute("match", match);
        model.addAttribute("job", job);
        return "candidate/match-analysis";
    }

    @PostMapping("/apply/{jobId}")
    public String apply(@PathVariable Long jobId, @AuthenticationPrincipal UserDetails ud, RedirectAttributes ra) {
        try {
            CandidateProfile p = getProfile(ud);
            Job job = jobRepository.findById(jobId).orElseThrow();
            applicationService.applyToJob(p, job);
            ra.addFlashAttribute("successMessage", "Application submitted successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/candidate/applications";
    }

    @PostMapping("/save/{jobId}")
    public String saveJob(@PathVariable Long jobId, @AuthenticationPrincipal UserDetails ud, RedirectAttributes ra) {
        CandidateProfile p = getProfile(ud);
        Job job = jobRepository.findById(jobId).orElseThrow();
        boolean saved = applicationService.toggleSaveJob(p, job);
        ra.addFlashAttribute("successMessage", saved ? "Job saved to your portfolio." : "Job removed from saved list.");
        return "redirect:/candidate/saved-jobs";
    }

    @GetMapping("/saved-jobs")
    public String savedJobs(@AuthenticationPrincipal UserDetails ud, Model model) {
        CandidateProfile p = getProfile(ud);
        model.addAttribute("savedJobs", applicationService.getSavedJobsForCandidate(p));
        return "candidate/saved-jobs";
    }

    @GetMapping("/applications")
    public String applications(@AuthenticationPrincipal UserDetails ud, Model model) {
        CandidateProfile p = getProfile(ud);
        model.addAttribute("applications", applicationService.getApplicationsForCandidate(p));
        return "candidate/applications";
    }
}
