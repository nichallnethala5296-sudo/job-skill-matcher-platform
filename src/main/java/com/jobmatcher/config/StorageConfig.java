package com.jobmatcher.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class StorageConfig {
    @Value("${app.storage.upload-dir:./uploads/resumes}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        try {
            Path root = Paths.get(uploadDir);
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + uploadDir, e);
        }
    }

    public Path getUploadPath() {
        return Paths.get(uploadDir).toAbsolutePath().normalize();
    }
}