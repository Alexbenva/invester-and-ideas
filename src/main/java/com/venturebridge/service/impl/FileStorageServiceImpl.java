package com.venturebridge.service.impl;

import com.venturebridge.service.FileStorageService;
import com.venturebridge.util.FileNameUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path uploadRoot;

    public FileStorageServiceImpl(@Value("${venturebridge.upload-dir:uploads}") String uploadDir) {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @Override
    public String store(MultipartFile file, String folderPrefix) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            Files.createDirectories(uploadRoot.resolve(folderPrefix).normalize());
            String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
            String storedName = UUID.randomUUID() + "_" + FileNameUtils.sanitize(originalName);
            Path targetLocation = uploadRoot.resolve(folderPrefix).resolve(storedName).normalize();
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + folderPrefix + "/" + storedName;
        } catch (IOException exception) {
            log.error("Failed to store file", exception);
            throw new IllegalStateException("Could not store file: " + file.getOriginalFilename(), exception);
        }
    }
}