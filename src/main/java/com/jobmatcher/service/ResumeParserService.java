package com.jobmatcher.service;

import com.jobmatcher.entity.CandidateProfile;
import com.jobmatcher.entity.CandidateSkill;
import com.jobmatcher.entity.Skill;
import com.jobmatcher.exception.FileStorageException;
import com.jobmatcher.repository.CandidateProfileRepository;
import com.jobmatcher.repository.CandidateSkillRepository;
import com.jobmatcher.repository.SkillRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class ResumeParserService {

    private final CandidateProfileRepository candidateProfileRepository;
    private final CandidateSkillRepository candidateSkillRepository;
    private final SkillRepository skillRepository;

    private static final Map<String, String> SYNONYM_MAP = new HashMap<>();

    static {
        SYNONYM_MAP.put("SPRINGBOOT", "SPRING BOOT");
        SYNONYM_MAP.put("POSTGRES", "POSTGRESQL");
        SYNONYM_MAP.put("JS", "JAVASCRIPT");
        SYNONYM_MAP.put("REACT.JS", "REACT");
        SYNONYM_MAP.put("REACTJS", "REACT");
        SYNONYM_MAP.put("K8S", "KUBERNETES");
        SYNONYM_MAP.put("AMAZON WEB SERVICES", "AWS");
        SYNONYM_MAP.put("RESTFUL", "REST API");
    }

    public ResumeParserService(CandidateProfileRepository candidateProfileRepository,
                               CandidateSkillRepository candidateSkillRepository,
                               SkillRepository skillRepository) {
        this.candidateProfileRepository = candidateProfileRepository;
        this.candidateSkillRepository = candidateSkillRepository;
        this.skillRepository = skillRepository;
    }

    public String extractTextFromFile(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        if (filename == null) throw new FileStorageException("File name cannot be empty");

        String lower = filename.toLowerCase();
        if (lower.endsWith(".pdf")) {
            try (PDDocument doc = Loader.loadPDF(file.getInputStream().readAllBytes())) {
                if (doc.isEncrypted()) throw new FileStorageException("Cannot parse encrypted PDF");
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(doc);
                return (text == null || text.isBlank()) ? "[SCANNED_PDF_NO_TEXT]" : text;
            }
        } else if (lower.endsWith(".docx")) {
            try (XWPFDocument docx = new XWPFDocument(file.getInputStream());
                 XWPFWordExtractor extractor = new XWPFWordExtractor(docx)) {
                return extractor.getText();
            }
        } else {
            throw new FileStorageException("Unsupported format. Please upload PDF or DOCX.");
        }
    }

    @Transactional
    public List<CandidateSkill> parseAndSaveSkills(CandidateProfile profile, String extractedText, Path savedPath) {
        profile.setExtractedResumeText(extractedText);
        if (savedPath != null) {
            profile.setResumePath(savedPath.toString());
        }
        candidateProfileRepository.save(profile);
        candidateSkillRepository.deleteByCandidate(profile);

        if (extractedText == null || extractedText.isBlank() || extractedText.equals("[SCANNED_PDF_NO_TEXT]")) {
            return Collections.emptyList();
        }

        List<Skill> allKnownSkills = skillRepository.findAll();
        Set<String> matchedSkillNames = new HashSet<>();
        String normalizedResume = extractedText.toUpperCase();

        for (Skill s : allKnownSkills) {
            if (containsWord(normalizedResume, s.getNormalizedName())) {
                matchedSkillNames.add(s.getNormalizedName());
            }
        }

        for (Map.Entry<String, String> entry : SYNONYM_MAP.entrySet()) {
            if (containsWord(normalizedResume, entry.getKey())) {
                matchedSkillNames.add(entry.getValue());
            }
        }

        List<CandidateSkill> result = new ArrayList<>();
        for (String name : matchedSkillNames) {
            Skill skill = skillRepository.findByNormalizedName(name)
                    .orElseGet(() -> skillRepository.save(new Skill(name, "GENERAL")));
            result.add(candidateSkillRepository.save(new CandidateSkill(profile, skill, "RESUME_EXTRACTED")));
        }
        return result;
    }

    private boolean containsWord(String source, String word) {
        String regex = "(?i)\\b" + Pattern.quote(word) + "\\b";
        return Pattern.compile(regex).matcher(source).find();
    }

    public Path storeFileSafely(MultipartFile file, Path baseDir, Long userId) throws IOException {
        String original = file.getOriginalFilename();
        String ext = original != null && original.toLowerCase().endsWith(".pdf") ? ".pdf" : ".docx";
        String safeName = "resume_user_" + userId + "_" + System.currentTimeMillis() + ext;
        Path destination = baseDir.resolve(safeName).normalize().toAbsolutePath();

        if (!destination.startsWith(baseDir.toAbsolutePath())) {
            throw new FileStorageException("Security error: Invalid upload target directory.");
        }
        try (InputStream is = file.getInputStream()) {
            Files.copy(is, destination, StandardCopyOption.REPLACE_EXISTING);
        }
        return destination;
    }
}