package com.jobmatcher.controller;

import com.jobmatcher.dto.UserRegistrationDto;
import com.jobmatcher.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) model.addAttribute("errorMessage", "Invalid credentials.");
        if (logout != null) model.addAttribute("successMessage", "Signed out successfully.");
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registrationDto", new UserRegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String handleRegistration(@Valid @ModelAttribute("registrationDto") UserRegistrationDto dto,
                                     BindingResult result,
                                     Model model) {
        if (result.hasErrors()) return "register";
        try {
            userService.registerUser(dto);
            return "redirect:/login?registered=true";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "register";
        }
    }
}