package com.jobmatcher.service;

import com.jobmatcher.dto.UserRegistrationDto;
import com.jobmatcher.entity.CandidateProfile;
import com.jobmatcher.entity.User;
import com.jobmatcher.repository.CandidateProfileRepository;
import com.jobmatcher.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       CandidateProfileRepository candidateProfileRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.candidateProfileRepository = candidateProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(UserRegistrationDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already in use.");
        }
        User user = new User();
        user.setName(dto.getName().trim());
        user.setEmail(dto.getEmail().toLowerCase().trim());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setRole("ROLE_RECRUITER".equals(dto.getRole()) ? "ROLE_RECRUITER" : "ROLE_CANDIDATE");

        User savedUser = userRepository.save(user);

        if ("ROLE_CANDIDATE".equals(savedUser.getRole())) {
            CandidateProfile profile = new CandidateProfile(savedUser);
            profile.setHeadline("Software Developer");
            profile.setExperienceLevel("MID_LEVEL");
            candidateProfileRepository.save(profile);
        }
        return savedUser;
    }
}