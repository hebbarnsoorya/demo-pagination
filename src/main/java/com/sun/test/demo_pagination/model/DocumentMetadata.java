package com.sun.test.demo_pagination.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "document_metadata") // Ensure this matches your DB table name
@Data // This Lombok annotation generates getStatus(), setStatus(), setHtmlContent(), etc.
@NoArgsConstructor
@AllArgsConstructor
public class DocumentMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String status; // Resolves Error #2 (getStatus)

    /**
     * @Lob is critical here.
     * Standard VARCHAR usually limits to 255 chars,
     * but HTML documents can be much larger.
     */
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String htmlContent; // Resolves Error #1 (setHtmlContent)

    private LocalDateTime lastModified;
}
