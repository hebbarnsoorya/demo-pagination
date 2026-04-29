package com.sun.test.demo_pagination.repository;


import com.sun.test.demo_pagination.model.DocumentMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentMetadata, Long> {
    Optional<DocumentMetadata> findByFileName(String fileName);
}
