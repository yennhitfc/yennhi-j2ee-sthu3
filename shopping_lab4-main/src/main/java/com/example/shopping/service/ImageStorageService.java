package com.example.shopping.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImageStorageService {
    private final Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to create upload directory", e);
        }
    }

    public Path getUploadDir() {
        return uploadDir;
    }

    public Optional<String> store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Optional.empty();
        }

        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        if (filename.contains("..")) {
            return Optional.empty();
        }

        String extension = StringUtils.getFilenameExtension(filename);
        String storedName = UUID.randomUUID() + (extension != null ? "." + extension : "");
        Path target = uploadDir.resolve(storedName);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            return Optional.of(storedName);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to store image", e);
        }
    }
}
