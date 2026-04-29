package com.sun.test.demo_pagination.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "billing_documents")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String fileType;
    private String filePath;

    // To track your specific marking
    private String tagStatus; // e.g., "TAG-CASE#1"

    private LocalDateTime lastModified;
    private LocalDateTime reminderDate;
}
