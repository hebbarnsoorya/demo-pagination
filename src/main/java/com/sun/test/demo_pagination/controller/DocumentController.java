package com.sun.test.demo_pagination.controller;

import com.sun.test.demo_pagination.model.dto.DocumentUpdateDTO;
import com.sun.test.demo_pagination.repository.DocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.util.StringUtils;
import java.time.format.DateTimeFormatter;
import java.nio.file.*;

import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@RestController
@RequestMapping("/api/v1/docs")
//@CrossOrigin(origins = "http://localhost:3000") // Your React App URL
@CrossOrigin(origins = "http://localhost:3000",
        allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.OPTIONS, RequestMethod.DELETE, RequestMethod.HEAD, RequestMethod.TRACE})
public class DocumentController {

    @Autowired
    private DocumentRepository documentRepository;

    private final Path rootLocation = Paths.get("C:/sn/work-docs/uploads");

    //documentService
    // 1. READ: Fetches the file for the React Viewer
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> readFile(@PathVariable String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // TASK#290426A1157.4: Binary Download for External Editing
    // Improvement: Explicit attachment header for binary integrity
    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable String filename) {
        try {
            log.info("Requested Filename : {}",filename);
            Path file = rootLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(file.toUri());


            System.out.println("DEBUG: Attempting to read file from: " + file.toAbsolutePath());


            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveDocx(@RequestParam("file") MultipartFile file,
                                           @RequestParam("filename") String filename) {
        try {
            if (!filename.toLowerCase().endsWith(".docx")) {
                return ResponseEntity.badRequest().body("Only .docx files are supported.");
            }

            String cleanName = StringUtils.cleanPath(filename);
            Path currentFile = this.rootLocation.resolve(cleanName).normalize();

            // 1. VERSION CONTROL: Move existing to history before overwrite
            if (Files.exists(currentFile)) {
                Path historyDir = this.rootLocation.resolve("history").resolve(cleanName);
                Files.createDirectories(historyDir);

                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm_ss"));
                String versionedName = timestamp + "_" + cleanName;
                Path archivePath = historyDir.resolve(versionedName);

                Files.move(currentFile, archivePath, StandardCopyOption.ATOMIC_MOVE);
            }

            // 2. SAVE NEW VERSION
            Files.createDirectories(currentFile.getParent());
            Files.copy(file.getInputStream(), currentFile, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok("Document updated. Version archived. (TAG-CASE#1 ready)");

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process document: " + e.getMessage());
        }
    }

    @PostMapping("/save-any")
    public ResponseEntity<String> saveAnyFile(@RequestParam("file") MultipartFile file,
                                              @RequestParam("filename") String filename) {
        try {
            if (file.isEmpty()) return ResponseEntity.badRequest().body("File is empty");

            String cleanName = StringUtils.cleanPath(filename);
            Path currentFile = this.rootLocation.resolve(cleanName).normalize();

            if (Files.exists(currentFile)) {
                Path historyDir = this.rootLocation.resolve("history").resolve(cleanName);
                Files.createDirectories(historyDir);

                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
                String versionedName = timestamp + "_" + cleanName;
                Path archiveFile = historyDir.resolve(versionedName);

                Files.move(currentFile, archiveFile, StandardCopyOption.REPLACE_EXISTING);
            }

            Files.createDirectories(currentFile.getParent());
            Files.copy(file.getInputStream(), currentFile, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok("File saved. Previous version archived in history/ folder.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/content")
    public ResponseEntity<?> updateDocumentContent(
            @PathVariable Long id,
            @RequestBody DocumentUpdateDTO updateDTO) {

        return documentRepository.findById(id).map(doc -> {
            doc.setHtmlContent(updateDTO.getHtml());
            String currentStatus = doc.getStatus();
            if ("CREATED".equals(currentStatus) || "INITIATED".equals(currentStatus)) {
                doc.setStatus("PROGRESS");
            } else {
                doc.setStatus(updateDTO.getStatus());
            }
            doc.setLastModified(LocalDateTime.now());
            documentRepository.save(doc);
            return ResponseEntity.ok().body("Document updated successfully to " + doc.getStatus());
        }).orElse(ResponseEntity.notFound().build());
    }
}