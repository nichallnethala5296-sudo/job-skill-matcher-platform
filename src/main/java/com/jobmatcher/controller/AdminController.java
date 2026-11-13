package com.jobmatcher.controller;

import com.jobmatcher.entity.CourseResource;
import com.jobmatcher.entity.Skill;
import com.jobmatcher.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CourseResourceRepository courseResourceRepository;

    public AdminController(UserRepository userRepository,
                           JobRepository jobRepository,
                           SkillRepository skillRepository,
                           CourseResourceRepository courseResourceRepository) {
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.courseResourceRepository = courseResourceRepository;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("candidateCount", userRepository.countByRole("ROLE_CANDIDATE"));
        model.addAttribute("recruiterCount", userRepository.countByRole("ROLE_RECRUITER"));
        model.addAttribute("totalJobs", jobRepository.count());
        model.addAttribute("skills", skillRepository.findAll());
        model.addAttribute("resources", courseResourceRepository.findAll());
        return "admin/dashboard";
    }

    @PostMapping("/skills/add")
    public String addSkill(@RequestParam String normalizedName, @RequestParam String category, RedirectAttributes ra) {
        skillRepository.save(new Skill(normalizedName, category));
        ra.addFlashAttribute("successMessage", "Skill registered.");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/resources/add")
    public String addResource(@RequestParam String skillName, @RequestParam String title,
                              @RequestParam String provider, @RequestParam String url, RedirectAttributes ra) {
        courseResourceRepository.save(new CourseResource(skillName, title, provider, url));
        ra.addFlashAttribute("successMessage", "Upskilling resource added.");
        return "redirect:/admin/dashboard";
    }
}