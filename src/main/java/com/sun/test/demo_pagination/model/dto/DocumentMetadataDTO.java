package com.sun.test.demo_pagination.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DocumentMetadataDTO {
    private Long id;
    private String fileName;
    private String status;
    private String htmlContent;
    private LocalDateTime lastModified;

    public DocumentMetadataDTO(Long id, String fileName, String status, String htmlContent, LocalDateTime lastModified) {
        this.fileName = fileName;
        this.status = status;
        this.id = id;
        this.htmlContent = htmlContent;
        this.lastModified = lastModified;
    }
}
