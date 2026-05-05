package com.sun.test.demo_pagination.model.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DocumentDTO {
    private Long id;
    private String fileName;
    private String status;
    private String htmlContent;
    private LocalDateTime lastModified;

    public DocumentDTO(Long id, String fileName, String status,  String htmlContent, LocalDateTime lastModified) {
        this.fileName = fileName;
        this.status = status;
        this.id = id;
        this.htmlContent = htmlContent;
        this.lastModified = lastModified;
    }
}
