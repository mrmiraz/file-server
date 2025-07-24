package com.example.fileserver.repository;

import com.example.fileserver.domain.entity.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    void deleteByStorageName(String storageName);
}