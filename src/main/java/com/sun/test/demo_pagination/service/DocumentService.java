package com.sun.test.demo_pagination.service;

import com.sun.test.demo_pagination.model.DocumentMetadata;
import com.sun.test.demo_pagination.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository repository;
    private final Path rootLocation = Paths.get("C:/SOORYA/work-docs/uploads");

    @Transactional
    public void saveOrUpdateDocument(MultipartFile file, String filename, String tag) throws IOException {
        // 1. Physical Save (Overwrite)
        if (!Files.exists(rootLocation)) {
            Files.createDirectories(rootLocation);
        }

        Path destinationFile = rootLocation.resolve(filename).normalize();
        Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

        // 2. Database Update
        DocumentMetadata metadata = repository.findByFileName(filename)
                .orElse(new DocumentMetadata());

        metadata.setFileName(filename);
        //metadata.setFilePath(destinationFile.toString());
        //metadata.setFileType(file.getContentType());
        //metadata.setTagStatus(tag); // Assigning "TAG-CASE#1"
        metadata.setLastModified(LocalDateTime.now());

        repository.save(metadata);
    }
}
