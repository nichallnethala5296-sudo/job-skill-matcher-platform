package com.jobmatcher.service;

import com.jobmatcher.entity.*;
import com.jobmatcher.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private JobApplicationRepository applicationRepository;
    @Mock
    private SavedJobRepository savedJobRepository;
    @Mock
    private CandidateSkillRepository candidateSkillRepository;
    @Mock
    private SkillMatchingService skillMatchingService;

    @InjectMocks
    private ApplicationService applicationService;

    @Test
    void testPreventDuplicateApplication() {
        CandidateProfile candidate = new CandidateProfile();
        Job job = new Job();

        when(applicationRepository.existsByCandidateAndJob(candidate, job)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> {
            applicationService.applyToJob(candidate, job);
        });

        verify(applicationRepository, never()).save(any());
    }
}