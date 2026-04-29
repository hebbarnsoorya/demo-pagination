package com.sun.test.demo_pagination.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Data
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String status; // CREATED, PROGRESS, etc.

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String htmlContent;

    private LocalDateTime lastModified;

    private String lastModifiedBy;
}
