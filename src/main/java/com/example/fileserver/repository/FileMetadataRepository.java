package com.example.fileserver.repository;

import com.example.fileserver.domain.entity.GenPrivateFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FileMetadataRepository extends JpaRepository<GenPrivateFile, Long> {
    Optional<GenPrivateFile> findByWebUuid(UUID uuid);
}