package com.sun.test.demo_pagination.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DocumentDTO {
    private Long id;
    private String fileName;
    private String tagStatus;
    private LocalDateTime lastModified;
    private LocalDateTime reminderDate;
}
