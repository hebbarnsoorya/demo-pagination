package com.sun.test.demo_pagination.controller;

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


@RestController
@RequestMapping("/api/docs")
@CrossOrigin(origins = "http://localhost:3000") // Your React App URL
public class DocumentController {

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



// ... within DocumentController class ...

    @PostMapping("/save")
    public ResponseEntity<String> saveDocx(@RequestParam("file") MultipartFile file,
                                           @RequestParam("filename") String filename) {
        try {
            // Validate it's actually a .docx
            if (!filename.toLowerCase().endsWith(".docx")) {
                return ResponseEntity.badRequest().body("Only .docx files are supported.");
            }

            String cleanName = StringUtils.cleanPath(filename);
            Path currentFile = this.rootLocation.resolve(cleanName).normalize();

            // 1. VERSION CONTROL: Move existing to history before overwrite
            if (Files.exists(currentFile)) {
                // Create subfolder for this specific file in history
                Path historyDir = this.rootLocation.resolve("history").resolve(cleanName);
                Files.createDirectories(historyDir);

                // Create timestamped version
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm_ss"));
                String versionedName = timestamp + "_" + cleanName;
                Path archivePath = historyDir.resolve(versionedName);

                // Atomic move to history
                Files.move(currentFile, archivePath, StandardCopyOption.ATOMIC_MOVE);
            }

            // 2. SAVE NEW VERSION: This becomes the 'Current' file for the React Viewer
            Files.createDirectories(currentFile.getParent());
            Files.copy(file.getInputStream(), currentFile, StandardCopyOption.REPLACE_EXISTING);

            // Reference: TAG-CASE#1 logic applied
            return ResponseEntity.ok("Document updated. Version archived. (TAG-CASE#1 ready)");

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process document: " + e.getMessage());
        }
    }


    // 2. SAVE: Receives the edited file from React and overwrites the existing one
    @PostMapping("/save-any")
    public ResponseEntity<String> saveAnyFile(@RequestParam("file") MultipartFile file,
                                           @RequestParam("filename") String filename) {
        try {
            if (file.isEmpty()) return ResponseEntity.badRequest().body("File is empty");

            String cleanName = StringUtils.cleanPath(filename);
            Path currentFile = this.rootLocation.resolve(cleanName).normalize();

            // 1. Version Control Logic
            if (Files.exists(currentFile)) {
                // Create a 'history' directory
                Path historyDir = this.rootLocation.resolve("history").resolve(cleanName);
                Files.createDirectories(historyDir);

                // Generate a versioned filename (e.g., invoice_20260428_1305.docx)
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
                String versionedName = timestamp + "_" + cleanName;
                Path archiveFile = historyDir.resolve(versionedName);

                // Move the existing file to history before overwriting
                Files.move(currentFile, archiveFile, StandardCopyOption.REPLACE_EXISTING);
            }

            // 2. Save the new version as the 'Primary' file
            Files.createDirectories(currentFile.getParent());
            Files.copy(file.getInputStream(), currentFile, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok("File saved. Previous version archived in history/ folder.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    //Updated
    /*
    @PostMapping("/save")
    public ResponseEntity<String> saveFile(@RequestParam("file") MultipartFile file,
                                           @RequestParam("filename") String filename) {
        try {
            // Calling service with the required TAG
            documentService.saveOrUpdateDocument(file, filename, "TAG-CASE#1");
            return ResponseEntity.ok("File and Metadata saved successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    */
}
