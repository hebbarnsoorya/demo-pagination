package com.sun.test.demo_pagination.controller;

import com.sun.test.demo_pagination.service.GoogleDriveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/docs")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class GoogleDocController {

    @Autowired
    private GoogleDriveService driveService;

    /**
     * API: /export-html/{filename}
     * Maps to React: documentService.fetchDocumentAsHtml
     */
    @GetMapping("/export-html/{filename}")
    public ResponseEntity<Map<String, String>> exportHtml(@PathVariable String filename) {
        try {
            // 1. Resolve Drive ID (Logical mapping)
            String fileId = driveService.getFileIdByName(filename);

            // 2. Stream HTML from Google export
            String htmlContent = driveService.exportDocToHtml(fileId);

            Map<String, String> response = new HashMap<>();
            response.put("html", htmlContent);
            response.put("filename", filename);
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorRes = new HashMap<>();
            errorRes.put("error", "Failed to export from Drive: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorRes);
        }
    }

    /**
     * API: /import-html
     * Maps to React: documentService.saveHtmlAsDocx
     */
    @PostMapping("/import-html")
    public ResponseEntity<Map<String, String>> importHtml(@RequestBody Map<String, String> payload) {
        String html = payload.get("html");
        String filename = payload.get("filename");
        Map<String, String> response = new HashMap<>();

        try {
            // 1. Resolve Drive ID
            String fileId = driveService.getFileIdByName(filename);

            // 2. Execute POI Transformation + Drive Update in one transactional service call
            driveService.updateFileFromHtml(fileId, html);

            response.put("message", "Successfully synced with Google Drive");
            response.put("filename", filename);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Sync failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}